package com.cyhd.common.util;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

/**
 * hash表示意图：hash到一个slot下对应一条 cacheBlock 的双向链表。如果一条key-value数据在一个cacheBlock放不下,则用一个单向cacheBlock链表来存储。
 * [0]
 * [1]--> [cacheBlock:entry1:一个块能完全放下]<-->[cacheBlock:entry2:一个块放不下]<-->[cacheBlock:entry3:一个块能完全放下]
 * [2]                                                   |
 * [3]                                           [cacheBlock:entry2:一个块放不下剩余的块链表]
 * [4]                                                   |
 * [5]                                           [cacheBlock:entry2:一个块放不下剩余的块链表]
 * [6]
 * [7]
 * [8]
 * [9]--> [cacheBlock:entry4:一个块能完全放下]<-->[cacheBlock:entry5:一个块能完全放下]
 * [10]
 * [11]--> [cacheBlock:entry6:一个块能完全放下]
 * [12]
 * [13]
 * [14]
 * 
 * 
 * 
 * cacheBlock 的格式: 第1个block才有下列全部字段,其它块只有第1个字段.
 * =========================================
 * 4B  block next pointer. -1 is last.
 * 
 * 4B  entry previous pointer.
 * 4B  entry next pointer.
 * 
 * 4B  lru list entry pointer.
 * 4B  key-length.
 * 4B  value-length.
 * 8B  seq.
 * key-length key.
 * value-length value.
 * 
 * 
 * =========================================
 * 
 * lru list 中 entry 的格式:
 * ==========================================
 * 4B previous entry pointer. -1 is null.
 * 4B next entry pointer. -1 is null.
 * 4B data map entry pointer. 
 * 4B data map entry hashBucketPointer.
 * ==========================================
 * 说明:data map 中hash到同一个slot的entry的list 和   lru list 中的entry 都用双向循环链表.head的前一个指向tail,tail的下一个指向head.
 * 这样是为了实现简单. 但 同一个data entry 的 block list 就是一个单向的 list.
 * */
public class BigCacheHashMap {
	protected int maxElements = 0;
	protected int size = 0;
	
	protected boolean lru = false;
	protected int keyType = 0;   // 0:byte[] , 1:long
	protected int valueType = 0; // 0:byte[] , 1:long
	
	
	protected static final int DEFAULT_CACHE_BLOCK_SIZE = 1024*2; // 2KB.
	private int cacheBlockSize = DEFAULT_CACHE_BLOCK_SIZE;
	private static final int DEFAULT_MAX_ENTRY_SIZE = 1024*512; // 512KB.
	
	private static final int BufferMaxBytes = 1024*2*1000000; // less than 2GB.

	protected ByteBuffer cacheBlockBuffer = null;
	
	/**
	 * idle block pointer[]
	 * */
	private ByteBuffer idleBlockMap = null;  
	/**
	 * index of idle block pointer array.
	 * */
	public int idleIndex = 0;  
	
	private int hashBucketSize = 0;  
	private ByteBuffer hashBucket = null;  
	
	
	// 返回码
	public static final int OK = 200;
	public static final int DATA_NO_EXIST = 404;
	public static final int KEY_ERRS = 2;
	public static final int PARAM_ERRS = 6;
	public static final int VALUE_ERRS = 5;
	public static final int NO_IDLE_BLOCKS = 3;
	public static final int CAS_ERR = 4;
	
	public BigCacheHashMap(int maxElements ){
		this(maxElements ,  DEFAULT_CACHE_BLOCK_SIZE);
	}
	public BigCacheHashMap(int maxElements , int cacheBlockSize ){
		this.maxElements = maxElements;
		this.cacheBlockSize = cacheBlockSize;
		
		try {
			init();
		} catch (Exception e) {
			System.out.println("init fail.");
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 
	 * 一次性初始化map
	 * @throws Exception 
	 * 
	 * */
	private void init() throws Exception{
		while(
				(((long)maxElements*(long)cacheBlockSize) > (long)BufferMaxBytes )
				|| (((long)maxElements*6) > (long)BufferMaxBytes) 
				|| (((long)maxElements*4) > (long)BufferMaxBytes)
			 ){
			maxElements = (int)(maxElements-100);
		}
		
		hashBucketSize = (int)(maxElements*1.5);
//		hashBucketSize = 1;
		hashBucket = ByteBuffer.allocateDirect(hashBucketSize*4);
		for(int i=0;i<hashBucketSize;i++){
			hashBucket.putInt(i*4, -1);
		}
		
		cacheBlockBuffer = ByteBuffer.allocateDirect(maxElements*cacheBlockSize);
		
		idleBlockMap = ByteBuffer.allocateDirect(maxElements*4);
		for(int i=0;i<maxElements;i++){
			idleBlockMap.putInt(i*4, i*cacheBlockSize);
		}
		idleIndex = maxElements-1;
		
		long total = idleBlockMap.capacity()+hashBucket.capacity()+cacheBlockBuffer.capacity();
		
		System.out.println("init ok:"+(int)((total/1024)/1024)+"MB." +
				" cacheBlockSize:"+cacheBlockSize+"byte maxElements:"+maxElements);
	}
	public int getMaxElements(){
		return maxElements;
	}
	public int getSize(){
		return size;
	}
	
	public byte[] get(byte[] key){
		if(key==null || key.length==0 || key.length>20){
			return null;
		}
		return this.get(key, 0, true);
	}
	public byte[] get(byte[] key, boolean isBizGet){
		if(key==null || key.length==0 || key.length>20){
			return null;
		}
		return this.get(key, 0, isBizGet);
	}
	public byte[] get(long key){
		return this.get(null, key, true);
	}
	/**
	 * 
	 * 目前的简化版是每个entry只在一个 block 中。不会超出。
	 * 每次调用会new分配一个临时的能够存放value的byte[].
	 * 
	 * */
	private byte[] get(byte[] key, long keyLong, boolean isBizGet){
		int hashCode = getHashCode(key);
		int blockPointer = hashBucket.getInt(4*(hashCode%hashBucketSize));
		if(blockPointer == -1){
			return null;
		}

		int listHead = blockPointer;
		boolean isFound = false;
		while(true){
			if(isSameKey(key, blockPointer)==false){
				blockPointer = cacheBlockBuffer.getInt(blockPointer+8);
				if(blockPointer == listHead){
					break;
				}
			}else{
				isFound = true;
				break;
			}
		}
		
		if(isFound == false){
			return null;
		}
		
		long seq = cacheBlockBuffer.getLong(blockPointer+24);
		int keyLength = cacheBlockBuffer.getInt(blockPointer+16);
		int valueLength = cacheBlockBuffer.getInt(blockPointer+20);
		
		
		byte[] result = new byte[valueLength];
		this.getValue(blockPointer, valueLength, keyLength, 32, result);
		
		if(isBizGet){
			moveToListFirst(-1, blockPointer, -1);
		}
		return result;
	}
	
	/**
	 * 用于遍历用<br>
	 * 参数是 0 到 (hashBucketSize-1)<br>
	 * 返回值是hashBucket上一个槽位上链表中的所有key.<br>
	 * 该方法的调用会产生临时垃圾对象.<br>
	 * */
	public List<byte[]> getSlotKeys(int hashBucketSlotNum){
		int blockPointer = hashBucket.getInt(4*(hashBucketSlotNum));
		if(blockPointer == -1){
			return null;
		}
		LinkedList<byte[]> result = new LinkedList<byte[]>();
		
		int listHead = blockPointer;
		while(true){
			int keylength = cacheBlockBuffer.getInt(blockPointer+16);
			byte[] key = new byte[keylength];
			int pointerBase = blockPointer+32;
			for(int j=0;j<keylength;j++){
				key[j]=cacheBlockBuffer.get(pointerBase+j);
			}
			result.add(key);
			
			blockPointer = cacheBlockBuffer.getInt(blockPointer+8);
			if(blockPointer == listHead){
				break;
			}
		}
		
		return result;
	}
	
	public void printMap(){
		System.out.println("=======dump map begin: maxElements:"+this.getMaxElements()+" size:"+this.getSize()+"==========");
		for(int i=0;i<hashBucketSize;i++){
			List<byte[]> list = getSlotKeys(i);
			if(list == null || list.size() == 0){
				continue;
			}
			StringBuilder sb = new StringBuilder();
			for(byte[] key : list){
				byte[] value = this.get(key, false);
				if(value == null){
					continue;
				}
				sb.append(new String(key)+" "+new String(value)+" | ");
			}
			if(sb.length() > 2){
				System.out.println(sb.toString());
			}
		}
		System.out.println("=======dump map finish: maxElements:"+this.getMaxElements()+" size:"+this.getSize()+"==========");
	}
	public void printLRUList(){
		System.out.println("=======no lru==========");
	}
	
	/**
	 * 返回此次写操作需要分配的block数量
	 * */
	private int calculateNeedBlockNum(int prexfixBytes, int keyLength, int valueLength){
		
		int allBytes = prexfixBytes + keyLength + valueLength - 4;
		int needBlockNum = (int)Math.ceil((double)allBytes/(cacheBlockSize-4));
		return needBlockNum;
	}
	public int set(byte[] key , byte[] value){
		if(value==null || value.length==0 || value.length+48>=cacheBlockSize){
//			return BigCacheHashMap.VALUE_ERRS;
		}
		long seq = 20131126;
		
		if(lru){
			int needBlockNum = calculateNeedBlockNum(32, key.length, value.length);
			while(idleIndex - needBlockNum < 0){
				releaseFromListTail();
			}
		}
		return this.update(key, value, seq, false, false, false, true);
	}
	
	public int setCas(byte[] key , byte[] value , long seq){
		if(value==null || value.length==0 || value.length+48>=cacheBlockSize){
//			return BigCacheHashMap.VALUE_ERRS;
		}
		if(lru){
			int needBlockNum = calculateNeedBlockNum(32, key.length, value.length);
			while(idleIndex - needBlockNum < 0){
				releaseFromListTail();
			}
		}
		return this.update(key, value, seq, true, false, false, true);
	}
	
	public int add(byte[] key , byte[] value ){
		if(value==null || value.length==0 || value.length+48>=cacheBlockSize){
//			return BigCacheHashMap.VALUE_ERRS;
		}
		long seq = 20131126;
		if(lru){
			int needBlockNum = calculateNeedBlockNum(32, key.length, value.length);
			while(idleIndex - needBlockNum < 0){
				releaseFromListTail();
			}
		}
		return this.update(key, value, seq, false, true, false, false);
	}
	
	public int delete(byte[] key){
		return this.update(key, null, 0, false, false, true, false);
	}
	
	public int deleteByEntryPointer(int findBlockPointer, int hashBucketPointer){
		if(findBlockPointer == -1){
			return BigCacheHashMap.PARAM_ERRS;
		}
		
		unlinkOneEntry(findBlockPointer, hashBucketPointer);
		removeListEntry(findBlockPointer);
		size--;
		return BigCacheHashMap.OK;
	}
	
	/**
	 * delete,add,cas set,set <br>
	 * 目前的简化版是每个entry只在一个 block 中。不会超出。
	 * */
	private int update(byte[] key , byte[] value, long seq ,boolean isCas ,boolean isAdd ,boolean isDelete ,boolean isSet){
		if(key==null || key.length==0 || key.length>20){
			return BigCacheHashMap.KEY_ERRS;
		}
		
		// ======== step1:寻找这个key是否存在 ========= //
		int findBlockPointer = -1;     // 被找到的entry的指针
		int hashCode = getHashCode(key);
		int hashBucketPointer = (hashCode%hashBucketSize)*4;
		int blockPointer = hashBucket.getInt(hashBucketPointer);
		if(blockPointer != -1){
			int listHead = blockPointer;
			while(true){
				if(isSameKey(key, blockPointer)==false){
					blockPointer = cacheBlockBuffer.getInt(blockPointer+8);
					if(blockPointer == listHead){
						break;
					}
				}else{
					findBlockPointer = blockPointer;
					break;
				}
			}
		}
		// ======== step1:finish ========= //
		
		// ======== step2:具体的操作  ========= //
		if(isDelete){
			if(findBlockPointer == -1){ // 没找到这个key.
				return BigCacheHashMap.DATA_NO_EXIST;
			}
			unlinkOneEntry(findBlockPointer, hashBucketPointer);
			removeListEntry(findBlockPointer);
			size--;
			return BigCacheHashMap.OK;
		}
		
		if(isAdd){
			if(findBlockPointer != -1){ // 找到这个key了.
				return BigCacheHashMap.CAS_ERR;
			}
			
			int newBlockPointer = allocteNewEntry( key ,  value,  seq);
			if(newBlockPointer < 0){
				return -1*newBlockPointer;
			}
			linkOneEntry(newBlockPointer , hashBucketPointer);
			moveToListFirst(newBlockPointer, -1, hashBucketPointer);
			size++;
			return BigCacheHashMap.OK;
		}
		
		if(isSet){
			if(isCas){ // cas check.
				if(findBlockPointer != -1){ // 这到key了
					long seqOld = cacheBlockBuffer.getLong(findBlockPointer+32);
					if(seq != seqOld){ // 不是之前的seq
						return BigCacheHashMap.CAS_ERR;
					}
				}
			}
			
			int newBlockPointer = allocteNewEntry( key ,  value,  seq);
			if(newBlockPointer < 0){
				return -1*newBlockPointer;
			}
			unlinkOneEntry(findBlockPointer, hashBucketPointer);
			linkOneEntry(newBlockPointer , hashBucketPointer);
			moveToListFirst(newBlockPointer, findBlockPointer, hashBucketPointer);
			
			if(findBlockPointer == -1){
				size++;
			}
			
			return BigCacheHashMap.OK;
		}
		
		return BigCacheHashMap.OK;
	}
	
	/**
	 * entry list 上增加一个新的entry.<br>
	 * 1:修改 entry list 链表,新来的放在list的头部.<br>
	 * 2:更新第1个entry中的字段 entry list size + 1 <br>
	 * ============================================<br>
	 * 
	 * */
	private void linkOneEntry(int newBlockPointer , int hashBucketPointer){
		int listHead = hashBucket.getInt(hashBucketPointer);
		if(listHead == -1){ // 第一个节点
			// 新节点的前后指针复制
			cacheBlockBuffer.putInt(newBlockPointer+4, newBlockPointer);
			cacheBlockBuffer.putInt(newBlockPointer+8, newBlockPointer);
			
			// 更新listHead指向
			hashBucket.putInt(hashBucketPointer, newBlockPointer);
		}else{
			int head = listHead;
			int tail = cacheBlockBuffer.getInt(listHead+4);
			
			// 新节点的前后指针复制
			cacheBlockBuffer.putInt(newBlockPointer+4, tail);
			cacheBlockBuffer.putInt(newBlockPointer+8, head);
			
			// 更新老head的指针
			cacheBlockBuffer.putInt(head+4, newBlockPointer);
			// 更新老tail的指针
			cacheBlockBuffer.putInt(tail+8, newBlockPointer);
			// 更新listHead指向
			hashBucket.putInt(hashBucketPointer, newBlockPointer);
		}
	}
	
	/**
	 * 回收一个entry.<br>
	 * 1:从链表摘除.<br>
	 * 2:回收这个entry占用的内存.<br>
	 * ============================<br>
	 * */
	private void unlinkOneEntry(int blockPointer, int hashBucketPointer){
		if(blockPointer == -1){
			return ;
		}
		
		int prePointer = cacheBlockBuffer.getInt(blockPointer+4);
		int nextPointer = cacheBlockBuffer.getInt(blockPointer+8);
		if(prePointer == blockPointer && nextPointer == blockPointer){ // 就自己一个.
			hashBucket.putInt(hashBucketPointer, -1);
		}else{
			// 从链表摘除
			cacheBlockBuffer.putInt(prePointer+8, nextPointer);
			cacheBlockBuffer.putInt(nextPointer+4, prePointer);
			
			// 处理恰巧是第一个节点的情况
			int listHead = hashBucket.getInt(hashBucketPointer);
			if(blockPointer == listHead){
				hashBucket.putInt(hashBucketPointer, nextPointer);
			}
		}
		
		// 回收内存
		releaseDataBlock(blockPointer);
	}
	
	/**
	 * 返回新分配的entry的第1个block的指针.
	 * 如果  < 0 , 分配失败,则放弃之前申请的内存.
	 * */
	private int allocteNewEntry(byte[] key , byte[] value, long seq){
		int newBlockPointer = allocDataBlock(value, key.length, 32);
		if(newBlockPointer < 0){
			return -1*BigCacheHashMap.NO_IDLE_BLOCKS;
		}
		
//		cacheBlockBuffer.putInt(newBlockPointer,   -1);
		cacheBlockBuffer.putInt(newBlockPointer+4, -1);
		cacheBlockBuffer.putInt(newBlockPointer+8, -1);
		cacheBlockBuffer.putInt(newBlockPointer+12, -1);
		
		cacheBlockBuffer.putInt(newBlockPointer+16, key.length);
		cacheBlockBuffer.putInt(newBlockPointer+20, value.length);
		cacheBlockBuffer.putLong(newBlockPointer+24, seq);
		
		int keyPointerBase = newBlockPointer+32;
		for(int i=0;i<key.length;i++){
			cacheBlockBuffer.put(keyPointerBase+i, key[i]);
		}
		
		return newBlockPointer;
	}
	
	private boolean isSameKey(byte[] key , int blockPointer){
		int keyLength = cacheBlockBuffer.getInt(blockPointer+16);
		if(key.length != keyLength){
			return false;
		}
		
		int pointerBase = blockPointer+32;
		for(int i=0;i<keyLength;i++){
			if(key[i]==cacheBlockBuffer.get(pointerBase+i)){
				// continue
			}else{
				return false;
			}
		}
		return true;
	}
		
	private int getHashCode(byte[] key){
		int hash = 0;
		int h = hash;
		if (h == 0) {
		    int off = 0;
		    int len = key.length;
		    for (int i = 0; i < 20; i++) {
		    	byte tmp = (i<len)?key[off++]:0x00;
		    	h = 31*h + tmp;
            }
            hash = h;
        }
		if(h < 0){
			return -h;
		}
	    return h;
	}
	
	// ================ 涉及到多个块的操作 ==================== //
	public byte[] getValue(int blockPointer, int valueLength, int keyLength, int prexfixBytes, byte[] result){
		int filled = 0;
		boolean isFirstBlock = true;
		while(filled < valueLength){
			int pointerBlockBase = 0;
			int length = 0;
			if(isFirstBlock){
				pointerBlockBase = blockPointer + prexfixBytes + keyLength;
				length = cacheBlockSize - prexfixBytes - keyLength;
				isFirstBlock = false;
			}else{
				pointerBlockBase = blockPointer + 4;
				length = cacheBlockSize - 4;
			}
			
			for(int i=0;i<length;i++){
				result[filled] = cacheBlockBuffer.get(pointerBlockBase+i);
				filled++;
				if(filled == valueLength){
					return result;
				}
			}
			blockPointer = cacheBlockBuffer.getInt(blockPointer);
			if(blockPointer == -1){ // list 中的最后一个节点
				break;
			}
		}
		return result;
	}
	/**
	 * 返回第一个block的指针.
	 * 如果返回值小于0,则其绝对值是需要分配的block数.
	 * */
	public int allocDataBlock(byte[] value, int keyLength, int prexfixBytes){
		int valueLength = value.length;
		
		// 因为每一个block的前4B都不能存储数据
		int allBytes = prexfixBytes + keyLength + valueLength - 4;
		int needBlockNum = (int)Math.ceil((double)allBytes/(cacheBlockSize-4));
		
		if(idleIndex - needBlockNum < 0){
			return -1*needBlockNum;
		}
		
		int filled = 0;
		int blockPointer = idleBlockMap.getInt(idleIndex*4);
		idleIndex--;
		
		int firstBlockPointer = blockPointer;
		boolean isFirstBlock = true;
		while(filled < valueLength){
			int pointerBlockBase = 0;
			int length = 0;
			if(isFirstBlock){
				pointerBlockBase = blockPointer + prexfixBytes + keyLength;
				length = cacheBlockSize - prexfixBytes - keyLength;;
				isFirstBlock = false;
			}else{
				pointerBlockBase = blockPointer + 4;
				length = cacheBlockSize - 4;
			}
			
			for(int i=0;i<length;i++){
				cacheBlockBuffer.put(pointerBlockBase+i, value[filled]);
				filled++;
				if(filled == valueLength){
					// 执行到这里说明在当前block就把数据全部复制完了.
					// 即:这个块就是最后一个block了,
					// 最后一个block的next指针是-1.
					cacheBlockBuffer.putInt(blockPointer, -1);
					return firstBlockPointer;
				}
			}
			
			// 执行到这里说明一次block的拷贝都已经结束了,而且
			// 还没有复制完,还需要申请下一个块.
			int lastBlockPointer = blockPointer;
			blockPointer = idleBlockMap.getInt(idleIndex*4);
			idleIndex--;
			if(blockPointer == -1 || idleIndex < 0){
				return -1; // 不可能执行到这里
			}
			
			cacheBlockBuffer.putInt(lastBlockPointer, blockPointer);
		}
		return -1; // 不可能执行到这里
	}
	public void releaseDataBlock(int blockPointer){
		while(true){
			idleIndex++;
			idleBlockMap.putInt(idleIndex*4,blockPointer);
			
			blockPointer = cacheBlockBuffer.getInt(blockPointer);
			if(blockPointer == -1){
				break;
			}
		}
	}
	
	/**
	 * LRU 相关.<br>
	 * @param newBlockPointer 先产生的map中的entry数据的指针.
	 * @param findBlockPointer 该entry在map中本来就存在.
	 * <br>
	 * 需要对新的指针的"lru list entry pointer"字段赋值.赋值的内容就是从已经存在(findBlockPointer)的老的entry中copy过来的.
	 * */
	public void moveToListFirst(int newBlockPointer, int findBlockPointer, int hashBucketPointer){
		// do nothing.
	}
	
	/**
	 * LRU 相关
	 * */
	public void removeListEntry(int findBlockPointer){
		// do nothing.
	}
	
	/**
	 * LRU 相关.<br>
	 * 从lru链表尾部开始释放数据.
	 * */
	public void releaseFromListTail(){
		// do nothing.
	}
}
