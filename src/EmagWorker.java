import java.io.*;
import java.util.concurrent.atomic.AtomicInteger;

public class EmagWorker implements Runnable{

	private final File productsFile;

	private final BufferedWriter productsWriter;

	private int productToFind;

	private String orderId;

	public EmagWorker(AtomicInteger productToProcess,
					  File productsFile,
					  BufferedWriter productsWriter,
					  String orderId) {
		this.productsFile = productsFile;
		this.productsWriter = productsWriter;
		this.productToFind = productToProcess.getAndIncrement();
		this.orderId = orderId;
	}

	@Override
	public void run() {

		int currentProduct = 0;
		String line;

		try (BufferedReader br = new BufferedReader(new FileReader(productsFile))) {
			line = br.readLine();
			while (currentProduct != productToFind) {
				if (line == null) break;

				String[] orderIdAndProduct = line.split(",");
				if (orderId.equals(orderIdAndProduct[0])) {
					currentProduct++;
				}

				if (currentProduct == productToFind) {
					productsWriter.write(line + ",shipped" + "\n");
				}
				line = br.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		Tema2.semaphore.release();
	}
}
