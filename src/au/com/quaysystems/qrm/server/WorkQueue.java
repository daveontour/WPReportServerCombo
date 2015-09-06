package au.com.quaysystems.qrm.server;

import java.util.LinkedList;

public class WorkQueue
{
	private final PoolWorker[] threads;
	@SuppressWarnings("rawtypes")
	private final LinkedList queue;
	
	public int getQueueLength(){
		return queue.size();
	}

	@SuppressWarnings("rawtypes")
	public WorkQueue(int nThreads) {
		queue = new LinkedList();
		threads = new PoolWorker[nThreads];

		for (int i=0; i<nThreads; i++) {
			threads[i] = new PoolWorker(i);
			threads[i].start();
		}
	}

	@SuppressWarnings("unchecked")
	public void execute(Runnable r) {
		synchronized(queue) {
			queue.addLast(r);
			System.out.println("Enqueuing Queue Length = "+queue.size());
			queue.notify();
		}
	}

	private class PoolWorker extends Thread {
		private int threadID;
		public PoolWorker(int i) {
			this.threadID = i;
		}

		public void run() {
			Runnable r;

			while (true) {
				synchronized(queue) {
					while (queue.isEmpty()) {
						try {
							queue.wait();
						} catch (InterruptedException ignored)  {}
					}

					r = (Runnable) queue.removeFirst();
					System.out.println("Report Thread "+this.threadID+ " Dequeuing Queue Length = "+queue.size());
				}

				try {
					r.run();
				} catch (RuntimeException e) {
					e.printStackTrace();
				}
			}
		}
	}
}