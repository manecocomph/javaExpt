package com.tianxiaohui.expt.thread;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ImplicitThreadPool {

	public static void main(String[] args) {
		List<String> names = Arrays.asList("Daniel", "Barry", "Eric", "Yuan");

		// 相当于 for loop. 这里都是当前 main 函数的主线程来执行这些 task. 不包括 call 里面的
		List<Callable<String>> tasks0 = names.stream().map(name -> {
			System.out.println("Before make callable task, run by " + Thread.currentThread().getName());
			return new Callable<String>() {
				public String call() throws Exception {
					System.out.println("In callable task0, run by " + Thread.currentThread().getName());
					return name.toUpperCase();
				}
			};
		}).collect(Collectors.toList());

		// 多线程合作 做这点事. 由 ForkJoinPool.commonPool-worker 线程池执行
		List<Callable<String>> tasks1 = names.parallelStream().map(name -> {
			System.out.println("Before make callable task, in parallelStream, run by " + Thread.currentThread().getName());
			return new Callable<String>() {
				public String call() throws Exception {
					System.out.println("In callable task1, run by " + Thread.currentThread().getName());
					return name.toUpperCase();
				}
			};
		}).collect(Collectors.toList());

		
		CompletableFuture.supplyAsync(() -> {
			System.out.println("0 - Run by " + Thread.currentThread().getName());
			return tasks1;
		}).thenApplyAsync(tasks -> {
			System.out.println("1 - Run by " + Thread.currentThread().getName());
			return tasks.stream().map(callable -> {
				try {
					System.out.println("2 - Run by " + Thread.currentThread().getName());
					return callable.call();
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			});
		}).exceptionally(e -> {
			e.printStackTrace();
			return null;
		}).join();
	}

	//以上的所有 Callable task 全部没有被执行
}
