
public class Producer extends Thread{
	
	private Cuby cubo;
	
	public Producer(Cuby cubo) {
		this.cubo = cubo;
	}
	
	@Override
	public void run() {
		for (int i = 0; i < Main.amount; i++) {
			cubo.put((int) (Math.random() * 100));
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ie) {
			}
		}
	}
}
