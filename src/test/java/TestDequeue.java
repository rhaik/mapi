import java.io.File;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedDeque;

import com.cyhd.common.util.Base64;


public class TestDequeue {

	public static void main(String[] args) throws Exception {
		
//		ConcurrentLinkedDeque<Integer> queue = new ConcurrentLinkedDeque<Integer>();
//		for(int i = 1; i < 10; i++){
//			queue.offer(i);
//		}
//		printQueue(queue);
//		queue.pop();
//		printQueue(queue);
//		queue.offer(10);
//		queue.pop();
//		printQueue(queue);
		
		
		String s = "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";
		
		byte[] bytes =Base64.decode(s);
		
		FileOutputStream fos = new FileOutputStream(new File("test.png"));
		
		
		fos.write(bytes);
		fos.close();
		
		String appName = "ganji";
		String target = "徒弟"; 
		
		//String alertBody = String.format("您的%s刚刚试用%s，赚了%s元，赚大钱给您奖励%s元。", target, appName, MoneyUtils.fen2yuanS(1000), MoneyUtils.fen2yuanS(2000));
		//System.out.println(alertBody);
	}
	
	private static void printQueue(ConcurrentLinkedDeque<Integer> queue){
		System.out.println("---------------------------------");
		Iterator<Integer> it = queue.descendingIterator();
		while(it.hasNext()){
			System.out.println(it.next());
		}
	}

}
