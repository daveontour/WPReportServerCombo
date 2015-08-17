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
			threads[i] = new PoolWorker();
			threads[i].start();
		}
	}

	@SuppressWarnings("unchecked")
	public void execute(Runnable r) {
		synchronized(queue) {
			queue.addLast(r);
			queue.notify();
		}
	}

	private class PoolWorker extends Thread {
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