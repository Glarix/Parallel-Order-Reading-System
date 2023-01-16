import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class Tema2 {

	static Semaphore semaphore;

	public static void main(String[] args) {
		File ordersFile = new File(args[0] + "orders.txt");
		File productsFile = new File(args[0] + "order_products.txt");

		int maxThreadsRunning = Integer.parseInt(args[1]);

		semaphore = new Semaphore(maxThreadsRunning);

		try {
			BufferedReader br = new BufferedReader(new FileReader(ordersFile));
			BufferedWriter writerOrders = new BufferedWriter(new FileWriter("orders_out.txt"));
			BufferedWriter writerProducts = new BufferedWriter(new FileWriter("order_products_out.txt"));
			ExecutorService executorService = Executors.newFixedThreadPool(maxThreadsRunning);

			for (int i = 0; i < maxThreadsRunning; i++) {
				executorService.submit(new EmagManager(i, br, writerOrders, writerProducts, productsFile));
			}

			executorService.shutdown();

			boolean finished = executorService.awaitTermination(10, TimeUnit.MINUTES);
			if (finished) {
				writerOrders.close();
				writerProducts.close();
				br.close();
			}

		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}



	}

}
