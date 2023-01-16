import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class EmagManager implements Runnable{

	private final int num;

	private final BufferedReader ordersFile;

	private final BufferedWriter ordersWriter;

	private final BufferedWriter productsWriter;

	private final File productsFile;

	private String orderId;

	private int numberOfProducts;

	private AtomicInteger productToProcess;

	public EmagManager(int num,
					   BufferedReader ordersFile,
					   BufferedWriter ordersWriter,
					   BufferedWriter productsWriter,
					   File productsFile) {
		this.num = num;
		this.ordersFile = ordersFile;
		this.ordersWriter = ordersWriter;
		this.productsWriter = productsWriter;
		this.productsFile = productsFile;

		productToProcess = new AtomicInteger(1);
	}

	public BufferedReader getOrdersFile() {
		return ordersFile;
	}

	public File getProductsFile() {
		return productsFile;
	}

	@Override
	public void run() {
		String line;

		while (true) {
			try {
				if (((line = ordersFile.readLine()) == null)) break;
				productToProcess.set(1);
				String[] tokens = line.split(",");

				orderId = tokens[0];
				numberOfProducts = Integer.parseInt(tokens[1]);
				if (numberOfProducts > 0) {
					ExecutorService executorService = Executors.newFixedThreadPool(numberOfProducts);

					for (int i = 0; i < numberOfProducts; i++) {
						Tema2.semaphore.acquire();
						executorService.submit(new EmagWorker(productToProcess, productsFile, productsWriter, orderId));
					}

					executorService.shutdown();

					boolean finished = executorService.awaitTermination(10, TimeUnit.MINUTES);
					if (finished) {
						ordersWriter.write(line + ",shipped" + "\n");
					}
				}
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}

		}
	}
}
