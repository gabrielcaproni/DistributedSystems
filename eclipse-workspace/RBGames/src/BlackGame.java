
public class BlackGame extends Thread{
private MegaSena mega;
	
	public BlackGame(MegaSena m) {
		this.mega = m;
	}
	
	@Override
	public void run() {
		for (int i = 0; i < mega.getGames(); i++) {
			
			//Lock
			while(Lock.lock.compareAndSet(0, 1) == false) System.out.print("");
			
			System.out.println("Black in.");
			mega.play(System.out);
			System.out.println("Black out.");
			System.err.flush();

			//Unlock
			Lock.lock.set(0);
		}
	}
}
