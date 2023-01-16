# EMAG BLACK FRIDAY

#### Codreanu Dan 331CB
---

## Description

This project is build to simulate a simplified version of how EMAG is handeling many order requests for the black friday period.

The program can:
 * Read orders from an input file concurrently and determine the order ID and the number of products

 * Read products from input file concurrently and find all products that correspond to the given order.

 * Ship (write in the output files) all the products and orders.
---

## Brief Class Description
* Tema2 - Class that starts the program and submits all the tasks to the Managers Thread Pool

* EmagManager - Class that handles orders and manages the workers into finding all the products from a order

* EmagWorker - Class that handles products from a given order.
---

## Functionality

At first the program creates the output buffers and the read orders buffer 
 * IMPORTANT NOTICE - BufferedWriter and BufferedReader are thread safe thus I did not used any special measures to make them usable with threads as they already are.

 A Semaphore is initialized so it can be used at Manager level.

 An ExecutorService is started with maxThreadsRunning as the fixed size and tasks are submited for each thread.

```java
int maxThreadsRunning = Integer.parseInt(args[1]);

ExecutorService executorService = Executors.newFixedThreadPool(maxThreadsRunning);
```

 The managers are taking the OrdersBuffer and start Executing orders one by one.

 ```java
 for (int i = 0; i < maxThreadsRunning; i++) {
	executorService.submit(new EmagManager(i, br, writerOrders, writerProducts, productsFile));
}
 ```

 A order is executed as follows: 
* The order Id is determined and Atomic Integer is set to 1 representing the product that needs to be found from the given order.

```java
productToProcess = new AtomicInteger(1);
```

* A pool of Workers is initiated with maximum of numberOfProducts Threads
```java
ExecutorService executorService = Executors.newFixedThreadPool(numberOfProducts);
```

* To submit a task, the manager tries to acquire from the static Semaphore
    * IMPORTANT NOTICE - The semaphore is used to make sure that the maximum number of threads running at Worker level is not greater than maxThreadsRunning
```java
for (int i = 0; i < numberOfProducts; i++) {
	// to make sure that no more than       admitted number of threads are running simultaneously at worker level I use this semaphore
	Tema2.semaphore.acquire();
	// submit the task to a worker in the pool
    executorService.submit(new EmagWorker(productToProcess, productsFile, productsWriter, orderId));
					}
```

* After all workers finished their job, the order is shipped (written to output file)

```java
boolean finished = executorService.awaitTermination(10, TimeUnit.MINUTES);
if (finished) {
    ordersWriter.write(line + ",shipped" + "\n");
}
```

* A worker gets the productToFind number from the atomic integer and increments it
```java
this.productToFind = productToProcess.getAndIncrement();
```

* Initiates a BufferedReader from OrderProducts file and starts scanning for the n-th product of the order

* Once found, the product is shipped (written to output file)


