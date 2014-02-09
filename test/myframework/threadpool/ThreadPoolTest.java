package myframework.threadpool;

import org.junit.Test;


public class ThreadPoolTest {


	@Test
	public void testExecute() {
		FixedThreadPool pool = new FixedThreadPool(6);
		for (int i = 0; i < 12; i++) {
			pool.execute(createTask(i));
		}
		pool.finish();
		pool.close();

	}
	
	/**
	 * Create task
	 * @param taskId
	 * @return
	 */
	private Runnable createTask(int taskId){
		return new Runnable() {
			@Override
			public void run() {
				System.out.println(Thread.currentThread().getName());
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
	}

}
