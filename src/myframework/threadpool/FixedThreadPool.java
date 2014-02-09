package myframework.threadpool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * A fixed thread pool based on ThreadGroup and BlockingQueue.
 * @author Fan
 * 
 */
public class FixedThreadPool implements Executor {

	private BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>();
	private ThreadGroup threadGroup = new ThreadGroup("FixedThreadPool");
	private volatile boolean isClosed = false;

	/**
	 * 
	 * @param size pool size
	 */
	public FixedThreadPool(int size) {
		threadGroup.setDaemon(true);
		for (int i = 0; i < size; i++) {
			new Thread(threadGroup, new Runnable() {
				@Override
				public void run() {
					while (!isClosed || !workQueue.isEmpty()) {
						try {
							workQueue.take().run();
						} catch (InterruptedException e) {
							return;
						}
					}
				}
			}, "Thread-" + i).start();
		}
	}

	/**
	 * Add a task to task queue
	 * 
	 * @param task task to execute
	 */
	@Override
	public void execute(Runnable task) {
		if (isClosed) {
			throw new IllegalStateException("Threadpool already closed.");
		}
		workQueue.add(task);
	}

	/**
	 * Finish all tasks in task queue
	 */
	public void finish() {
		isClosed = true;
		// Take active threads from thread group
		Thread[] threads = new Thread[threadGroup.activeCount()];
		int count = threadGroup.enumerate(threads);

		for (int i = 0; i < count; i++) {
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		threadGroup.interrupt();
	}

	/**
	 * Close thread pool
	 */
	public void close() {
		if (!isClosed) {
			isClosed = true;
			workQueue.clear();
			threadGroup.interrupt();
		}
	}

}
