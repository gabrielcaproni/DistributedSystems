import java.util.Random;

public class RandomB extends Thread{
	
	private String id;
	
	public RandomB(int id) {
		this.id = "B - ".concat(String.valueOf(id));
	}
	
	@Override
	public void run() {
		while(true) {
			try {
			//Operação P(s) - decremento
			Lock.lock.lock();
			
			int number = new Random().nextInt(1000);
			System.out.printf("RANDOM-%s\n", id);
			System.out.printf("%s -> %d\n", id, number);
			
			Thread.sleep(number);
			
			}catch(InterruptedException ie) {}
			finally {Lock.lock.unlock();}
		}
		}
	}

