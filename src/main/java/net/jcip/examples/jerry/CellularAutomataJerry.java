package net.jcip.examples.jerry;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class CellularAutomataJerry {

	private final Board mainBoard;
	private final CyclicBarrier barrier;
	private final Worker[] workers;

	public CellularAutomataJerry(Board board) {
		this.mainBoard = board;
		int count = Runtime.getRuntime().availableProcessors();
		this.barrier = new CyclicBarrier(count, new Runnable() {
			public void run() {
				mainBoard.commitNewValues();
			}
		});
		this.workers = new Worker[count];
		for(int i = 0;i < count; i++){
			workers[i] = new Worker(mainBoard.getSubBoard(count, i));
		}
	}

	private class Worker implements Runnable {
		private final Board board;

		public Worker(Board board) {
			this.board = board;
		}

		@Override
		public void run() {
			while (!board.hasConverged()) {
				for (int x = 0; x < board.getMaxX(); x++) {
					for (int y = 0; y < board.getMaxY(); y++) {
						board.setNewValue(x, y, computeValue(x, y));
					}
				}
				try {
					barrier.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (BrokenBarrierException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void start() {
		for (int i = 0; i < workers.length; i++) {
			new Thread(workers[i]).start();
		}
		mainBoard.waitForConvergence();
	}

	public interface Board {

		int getMaxX();

		int getMaxY();

		int getValue(int x, int y);

		int setNewValue(int x, int y, int value);

		void commitNewValues();

		boolean hasConverged();

		void waitForConvergence();

		Board getSubBoard(int numPartitions, int index);
	}

	public int computeValue(int x, int y) {
		return 0;
	}
}
