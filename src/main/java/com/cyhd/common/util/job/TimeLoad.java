package com.cyhd.common.util.job;

/**
 * 类似linux，记录过去时间负载：1分钟，5分钟，15分钟
 * 为了避免波动，不包括当前一分钟
 * 使用方式：
 * TimeLoad tl = new TimeLoad();
 * tl.request();	// 单次
 * tl.request(delta); // 带权值的
 *
 */
public class TimeLoad {

	public static final long ONE_MIN = 60*1000l;
	
	private static final int MAX = 16;
	private int[] counts = new int[MAX];	// 记录每分钟负载
	private int currIndex = 0;	// 当前索引
	private int oneIndex = 1;	// 1分钟索引
	private int fiveIndex = 5;	// 5分钟索引
	private int fifIndex = 15;	// 15分钟索引
	
	private int currLoad = 0;	// 当前一分钟负载
	private int oneLoad = 0;	// 1分钟负载
	private int fiveLoad = 0;	// 5分钟负载
	private int fifLoad = 0;	// 15分钟负载
	private int total = 0;		// 总请求量
	
	private long lastMin = System.currentTimeMillis();
	
	/**
	 * 请求一次
	 */
	public synchronized void request(){
		checkMin();
		// 一分钟之内，自动计数
		counts[currIndex]++;
		currLoad++;
		total++;
	}
	
	/**
	 * 带权值
	 */
	public synchronized void request(int delta){
		checkMin();
		// 一分钟之内，自动计数
		counts[currIndex] += delta;
		currLoad += delta;
		total += delta;
	}
	
	
	private void checkMin(){
		long curr = System.currentTimeMillis();
		if (curr >= (lastMin + ONE_MIN)){
			// 时间迭代
			oneLoad = currLoad;
			oneIndex--;
			if (oneIndex<0) oneIndex = MAX - 1;

			fiveLoad += currLoad;
			fiveLoad -= counts[fiveIndex];
			fiveIndex--;
			if (fiveIndex<0) fiveIndex = MAX - 1;

			fifLoad += currLoad;
			fifLoad -= counts[fifIndex];
			fifIndex--;
			if (fifIndex<0) fifIndex = MAX - 1;
			
			currLoad = 0;
			currIndex--;
			if (currIndex<0) currIndex = MAX - 1;
			lastMin = curr;
		}
	}
	
	public int getOneLoad(){ return oneLoad; }
	public int getFiveLoad(){ return fiveLoad; }
	public int getFifLoad(){ return fifLoad; }
	public int getTotal(){ return total; }
	
	public int getLoad(int min){
		if (min<=1) return oneLoad;
		if (min>=15) return fifLoad;
		int c = 0;
		if ((oneIndex+min)<=MAX){
			for(int i=oneIndex;i<(oneIndex+min);i++){
				c += counts[i];
			}
		}else {
			for(int i=oneIndex;i<MAX;i++){
				c += counts[i];
			}
			for(int i=0;i<(min+oneIndex-MAX);i++){
				c += counts[i];
			}
		}
		return c;
	}
	
	public String getLoad(){
		return String.format("TimeLoad: curr=%d, total=%d, load: %d, %d, %d", currLoad, total, oneLoad, fiveLoad, fifLoad);
	}
	public String getAverage(){
		return String.format("Average: %d, %.2f, %.2f", oneLoad, fiveLoad/5.0, fifLoad/15.0);
	}
	public String getDetail(){
		StringBuilder sb = new StringBuilder("Detail: [");
		sb.append(counts[oneIndex]);
		for(int i=oneIndex+1;i<MAX&&i!=currIndex;i++) sb.append(", ").append(counts[i]);
		for(int i=0;i<oneIndex-1;i++) sb.append(", ").append(counts[i]);
		sb.append("]");
		return sb.toString();
	}
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(getLoad()).append("\n");
		sb.append(getAverage()).append("\n");
		sb.append(getDetail());
		return sb.toString();
	}
	
	public static void main(String[] args) throws Exception{
		// 测试时，ONE_MIN = 1000l;
		TimeLoad tl = new TimeLoad();
		for(int i=1;i<31;i++){
			for(int j=0;j<i;j++){
				tl.request();
			}
			System.out.println("=========== "+i+" ============");
			System.out.println(tl);
			System.out.println(tl.getLoad(i));
			Thread.sleep(TimeLoad.ONE_MIN);
		}
	}
}
