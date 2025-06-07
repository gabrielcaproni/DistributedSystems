
public class Main {
	
	public static int amount = 10;

	public static void main(String[] args) {
		
		Cuby cubo = new Cuby();
		
		Producer produtor = new Producer(cubo);
		produtor.setName("Produtor");
		produtor.start();
		
		Consumer consumidor = new Consumer(cubo);
		consumidor.setName("Consumidor");
		consumidor.start();
		
		System.out.println("Fim thread 'main'.");
	}

}
