

public class Main {

	
	public static final int MAX = 1000;		
	public static void main(String[] args) {
		
	
		JobThread job1 = new JobThread();
		job1.setName("Job1");
		//coloca a thread em estado runnable
		job1.start();
		
		JobRunnable job2 = new JobRunnable();
		Thread threadJob2 = new Thread(job2);
		threadJob2.setName("Job 2");	
		threadJob2.start();
		
		
		
		//thread main
		String threadName = Thread.currentThread().getName();
		for (int i=0; i<1000 ; i++) {
			System.out.printf("%s - %d\n", threadName, i);
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {}
		}

}
}