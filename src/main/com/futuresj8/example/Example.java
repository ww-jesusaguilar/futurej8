package com.futuresj8.example;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.util.concurrent.*;
import org.jdeferred2.DeferredManager;
import org.jdeferred2.Promise;

import javax.xml.ws.Response;
import java.sql.Time;
import java.util.*;
import java.util.concurrent.*;

public class Example{

    public static Callable<String> callable(String result, long sleepSeconds) {
        return () -> {
            TimeUnit.SECONDS.sleep(sleepSeconds);
            return result;
        };
    }

    public static void ejecutarListaCallables() throws InterruptedException, ExecutionException{
        ExecutorService executor = Executors.newWorkStealingPool();

        List<Callable<String>> callables = Arrays.asList(
                callable("task1", 2),
                callable("task2", 1),
                callable("task3", 3));

        String result = executor.invokeAny(callables);
        System.out.println(result);
    }

    public static void ejecutarCallable () throws InterruptedException {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

        Runnable task = () -> System.out.println("Scheduling: " + System.nanoTime());
        ScheduledFuture<?> future = executor.schedule(task, 8, TimeUnit.SECONDS);

        TimeUnit.MILLISECONDS.sleep(6337);

        long remainingDelay = future.getDelay(TimeUnit.MILLISECONDS);
        System.out.println("Remaining Delay: " + remainingDelay + "sms");
    }

    public static void ejecutarRunnableSimple(){
        Runnable task = () -> {
            String threadName = Thread.currentThread().getName();
            System.out.println("Hello " + threadName);
        };

        task.run();

        Thread thread = new Thread(task);
        thread.start();

        System.out.println("Done!");
    }

    public static void ejecutarRunnableLambda(){
        Runnable runnable = () -> {
            try {
                String name = Thread.currentThread().getName();
                System.out.println("Foo " + name);
                TimeUnit.SECONDS.sleep(1);
                System.out.println("Bar " + name);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();
    }

    public static void ejecutarExecutor(){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            String threadName = Thread.currentThread().getName();
            System.out.println("Hello " + threadName);
        });
    }

    public static void ejecutarFutureCallable() throws ExecutionException, InterruptedException{
        Callable<Integer> task = () -> {
            try {
                TimeUnit.SECONDS.sleep(3);
                return 123;
            }
            catch (InterruptedException e) {
                throw new IllegalStateException("task interrupted", e);
            }
        };

        ExecutorService executor = Executors.newFixedThreadPool(1);
        Future<Integer> future = executor.submit(task);

        System.out.println("future done? " + future.isDone());

        Integer result = future.get();

        System.out.println("future done? " + future.isDone());
        System.out.print("result: " + result);
    }

    public static void ejecutarFutureTerminar() throws ExecutionException, InterruptedException{
        Callable<Integer> task = () -> {
            try {
                TimeUnit.SECONDS.sleep(3);
                return 123;
            }
            catch (InterruptedException e) {
                throw new IllegalStateException("task interrupted", e);
            }
        };

        ExecutorService executor = Executors.newFixedThreadPool(1);
        Future<Integer> future = executor.submit(task);

        System.out.println("future done? " + future.isDone());

        while(!future.isDone()){
            System.out.println("No esta listo...");
        }

        System.out.println("future done? " + future.isDone());

        Integer result = future.get();

        System.out.println("future done? " + future.isDone());
        System.out.print("result: " + result);
    }

    public static void ejecutarCompletableFuture(){
        CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(3);
                return 5;
            }
            catch (InterruptedException e) {
                throw new IllegalStateException("task interrupted", e);
            }
        }).thenApply(i -> i / 3)
          .thenAccept(i -> System.out.println("The result is " + i))
          .exceptionally(ex -> {System.out.println(ex.getMessage()); return null;});
    }

    public static void ejecutarCompletableFutureCombined() throws ExecutionException, InterruptedException{
        System.out.println("Retrieving weight.");
        CompletableFuture<Double> weightInKgFuture = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            return 90.0;
        });

        System.out.println("Retrieving height.");
        CompletableFuture<Double> heightInCmFuture = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            return 177.8;
        });

        System.out.println("Calculating BMI.");
        CompletableFuture<Double> combinedFuture = weightInKgFuture
                .thenCombine(heightInCmFuture, (weightInKg, heightInCm) -> {
                    Double heightInMeter = heightInCm/100;
                    return weightInKg/(heightInMeter*heightInMeter);
                });

        System.out.println("Your BMI is - " + combinedFuture.get());
    }

    public static void ejecutarListenableFuture() throws InterruptedException{
        ListeningExecutorService service = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(10));
        ListenableFuture<Integer> lFEntero = service.submit(new Callable<Integer>() {
            public Integer call() throws InterruptedException {
                TimeUnit.SECONDS.sleep(3);
                return (Integer) 15/3;
            }
        });
        Futures.addCallback(lFEntero, new FutureCallback<Integer>() {
            // we want this handler to run immediately after we push the big red button!
            public void onSuccess(Integer numero) {
                System.out.println("El cuadrado es: " + numero);
            }

            public void onFailure(Throwable thrown) {
                System.out.println("Error: " + thrown.getMessage()); // escaped the explosion!
            }
        }, service);
    }
    
    public static void ejecutarListenableFuturesEncadenados(){
        ListeningExecutorService service = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(10));
        ListenableFuture<Integer> integerFuture = service.submit(() -> {
            return 0;
        });
        AsyncFunction<Integer, String> queryFunction =
                new AsyncFunction<Integer, String>() {
                    public ListenableFuture<String> apply(Integer integer) {
                        return service.submit(() -> {
                            return "decimal " + 10000/integer;
                        });
                    }
                };
        ListenableFuture<String> queryFuture =
                Futures.transformAsync(integerFuture, queryFunction, MoreExecutors.directExecutor());

        Futures.addCallback(queryFuture, new FutureCallback<String>() {
            // we want this handler to run immediately after we push the big red button!
            public void onSuccess(String resultado) {
                System.out.println("El resultado es: " + resultado);
            }

            public void onFailure(Throwable thrown) {
                System.out.println("Error: " + thrown.getMessage()); // escaped the explosion!
            }
        }, service);
    }

    private DeferredManager deferredManager;

    public static Integer callable2(Integer result, long sleepSeconds) throws InterruptedException {
            TimeUnit.SECONDS.sleep(sleepSeconds);
            return result;
    }

    public Promise<Integer, Throwable, Void> repositories(final String organization) {
        return deferredManager.when(()  -> {
            Integer integer = -1;

            integer = callable2(28, 3);

            if (integer != -1) { return integer; }
            throw new IllegalStateException("Error");
        });
    }
}