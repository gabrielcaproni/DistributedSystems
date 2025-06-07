import java.io.PrintStream;
import java.util.Random;

public class MegaSena {
	
	private int numbersPerGames;
	private int games;
	
	private static final int MAX_VALUE = 60;
	
	public MegaSena(int numbers, int games) {
		this.numbersPerGames = numbers;
		this.games = games;
	}
	
	public int getGames () {
		return games;
	}
	
	public void play(PrintStream printer) {
		for (int i = 0; i < numbersPerGames; i++) {
			int number = new Random().nextInt(MAX_VALUE) + 1;
			printer.printf(String.format("%2d ", number));
		}
		printer.println();
	}
}
