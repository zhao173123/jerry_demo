package net.jcip.examples;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * ReaderThread
 * <p/>
 * Encapsulating nonstandard cancellation in a Thread by overriding interrupt
 * 包装非标准的取消操作
 * ReaderThread管理Socket套接字，采用同步的方式的读取数据
 *
 * @author Brian Goetz and Tim Peierls
 */
public class ReaderThread extends Thread {
	private static final int BUFSZ = 512;
	private final Socket socket;
	private final InputStream in;

	public ReaderThread(Socket socket) throws IOException {
		this.socket = socket;
		this.in = socket.getInputStream();
	}

	/**
	 * 改写interrupt
	 * 1.可以处理标准的中断
	 * 2.可以关闭底层的套接字
	 */
	public void interrupt() {
		try {
			socket.close();
		} catch (IOException ignored) {
		} finally {
			super.interrupt();
		}
	}

	public void run() {
		try {
			byte[] buf = new byte[BUFSZ];
			while (true) {
				int count = in.read(buf);
				if (count < 0)
					break;
				else if (count > 0)
					processBuffer(buf, count);
			}
		} catch (IOException e) { /* Allow thread to exit */
		}
	}

	public void processBuffer(byte[] buf, int count) {
	}
}
