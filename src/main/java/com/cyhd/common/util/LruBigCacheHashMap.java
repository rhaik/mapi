package com.cyhd.common.util;

import java.nio.ByteBuffer;


public class LruBigCacheHashMap extends BigCacheHashMap {
	
	private ByteBuffer LRUEntryAll = null;
	private ByteBuffer idleLRUEntryStack = null;
	private int idleLRUEntryIndex = 0;
	
	private int listHeadPointer = -1;
	
	public LruBigCacheHashMap(int maxElements ){
		this(maxElements ,  DEFAULT_CACHE_BLOCK_SIZE);
	}
	public LruBigCacheHashMap(int maxElements , int cacheBlockSize ){
		super(maxElements, cacheBlockSize);
		lru = true;
		try {
			init();
		} catch (Exception e) {
			System.out.println("init fail.");
			e.printStackTrace();
		}
	}
	
	private void init() throws Exception{
		LRUEntryAll = ByteBuffer.allocateDirect(maxElements*16);
		idleLRUEntryStack = ByteBuffer.allocateDirect(maxElements*4);
		for(int i=0;i<maxElements;i++){
			idleLRUEntryStack.putInt(i*4, i*16);
		}
		idleLRUEntryIndex = maxElements-1;
	
		long total = LRUEntryAll.capacity()+idleLRUEntryStack.capacity();
		
		System.out.println("init ok:"+(int)((total/1024)/1024)+"MB." +
				" byte maxElements:"+maxElements);
	}
	
	
	@Override
	public void moveToListFirst(int newBlockPointer, int findBlockPointer, int hashBucketPointer){
		if(findBlockPointer == -1){ // map之前没有找到,这个情况是map新增一个,隐含的意思是:list中也新增一个.
			int listEntryPointer = idleLRUEntryStack.getInt(idleLRUEntryIndex*4);
			idleLRUEntryIndex--;
			LRUEntryAll.putInt(listEntryPointer+8, newBlockPointer);
			LRUEntryAll.putInt(listEntryPointer+12, hashBucketPointer);
			cacheBlockBuffer.putInt(newBlockPointer+12, listEntryPointer);
			
			addFirstLinkList(listEntryPointer);
		}else{ // map中找到了,隐含的意思是:map中本来就有.list中也本来就有.
			int listEntryPointer = cacheBlockBuffer.getInt(findBlockPointer+12);
			if(listEntryPointer == -1){
				return ;
			}
			if(newBlockPointer != -1){ // 新分配的map中的entry来替换老的entry.
				cacheBlockBuffer.putInt(newBlockPointer+12, listEntryPointer);
			}
			
			if(listHeadPointer == listEntryPointer){ // 已经是第一个了
				return ;
			}
			
			removeFromLinkList(listEntryPointer);
			addFirstLinkList(listEntryPointer);
		}
	}
	
	/**
	 * 从链表中摘除一个
	 * */
	private void removeFromLinkList(int listEntryPointer){
		// ========== 把已存在的entry摘除
		int prevPointer = LRUEntryAll.getInt(listEntryPointer);   // 前一个的指针
		int nextPointer = LRUEntryAll.getInt(listEntryPointer+4); // 后一个的指针
		
		if(prevPointer == listEntryPointer && nextPointer == listEntryPointer){ // 就只有自己一个节点
			listHeadPointer = -1;
			return;
		}
		
		LRUEntryAll.putInt(prevPointer+4, nextPointer);
		LRUEntryAll.putInt(nextPointer, prevPointer);
		
		if(listEntryPointer == listHeadPointer){
			listHeadPointer = nextPointer;
		}
	}
	
	private void addFirstLinkList(int listEntryPointer){
		if(listHeadPointer == -1){ // 已经是第一个了
			LRUEntryAll.putInt(listEntryPointer, listEntryPointer);
			LRUEntryAll.putInt(listEntryPointer+4, listEntryPointer);
		}else{
			int tail = LRUEntryAll.getInt(listHeadPointer);
			int head = listHeadPointer;
			
			// ========== 对目标entry的指针赋值新值
			LRUEntryAll.putInt(listEntryPointer, tail);
			LRUEntryAll.putInt(listEntryPointer+4, head);
			
			// ========== 修改老head的指向
			LRUEntryAll.putInt(head, listEntryPointer);
			
			// ========== 修改老tail的指向
			LRUEntryAll.putInt(tail+4, listEntryPointer);
		}
		
		// ========== 修改 head 指针
		listHeadPointer = listEntryPointer;
	}
	
	@Override
	public void removeListEntry(int findBlockPointer){
		if(findBlockPointer == -1){
			return ;
		}
		
		int listEntryPointer = cacheBlockBuffer.getInt(findBlockPointer+12);
		if(listEntryPointer == -1){
			return ;
		}
		
		removeFromLinkList(listEntryPointer);
		
		// 回收entry
		idleLRUEntryIndex++;
		idleLRUEntryStack.putInt(idleLRUEntryIndex*4, listEntryPointer);
	}
	
	@Override
	public void releaseFromListTail(){
		if(listHeadPointer == -1){
			return;
		}
		int releasEntryPointer = LRUEntryAll.getInt(listHeadPointer);
		
		int dateBlockPointer = LRUEntryAll.getInt(releasEntryPointer+8);
		int hashBucketPointer = LRUEntryAll.getInt(releasEntryPointer+12);
		
		deleteByEntryPointer(dateBlockPointer, hashBucketPointer);
	}
	
	@Override
	public void printLRUList(){
		System.out.println("=======dump lru begin==========");
		if(listHeadPointer == -1){
			System.out.println("=======no list entry.==========");
			return;
		}
		int listEntryPointer = listHeadPointer;
		StringBuilder sb = new StringBuilder();
		while(true){
			int dateBlockPointer = LRUEntryAll.getInt(listEntryPointer+8);
			
			int keyLength = cacheBlockBuffer.getInt(dateBlockPointer+16);
			byte[] key = new byte[keyLength];
			int pointerBase = dateBlockPointer+32;
			for(int j=0;j<keyLength;j++){
				key[j]=cacheBlockBuffer.get(pointerBase+j);
			}
			sb.append(new String(key)+" | ");
			
			listEntryPointer = LRUEntryAll.getInt(listEntryPointer+4); // 后一个的指针
			if(listEntryPointer == listHeadPointer){
				break;
			}
		}
		System.out.println(sb.toString());
		System.out.println("=======dump lru finish==========");
	}

}
