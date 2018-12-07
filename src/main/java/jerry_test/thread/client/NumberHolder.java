package jerry_test.thread.client;

/**
 * 利用2个线程对一个整形变量进行变化 一个减少，一个增加 利用线程间的通讯，实现整形0101的变化
 * 
 * @author jerry
 *
 */
public class NumberHolder {

	private int number;

	public synchronized void increase() {
		while (0 != number) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		number++;
		System.out.println(number);
		notifyAll();
	}

	public synchronized void decrease() {
		while (0 == number) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		number--;
		System.out.println(number);
		notifyAll();
	}
}
