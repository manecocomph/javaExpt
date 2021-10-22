package com.tianxiaohui.expt.thread;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ImplicitThreadPool {

	public static void main(String[] args) {
		List<String> names = Arrays.asList("Daniel", "Barry", "Eric", "Yuan");

		// 相当于 for loop
		List<Callable<String>> tasks0 = names.stream().map(name -> {
			return new Callable<String>() {
				public String call() throws Exception {
					System.out.println("Run by " + Thread.currentThread().getName());
					return name.toUpperCase();
				}
			};
		}).collect(Collectors.toList());

		// 多线程合作 做这点事
		List<Callable<String>> tasks1 = names.parallelStream().map(name -> {
			return new Callable<String>() {
				public String call() throws Exception {
					System.out.println("Run by " + Thread.currentThread().getName());
					return name.toUpperCase();
				}
			};
		}).collect(Collectors.toList());

		CompletableFuture.supplyAsync(() -> {
			System.out.println("Run by " + Thread.currentThread().getName());
			return tasks1;
		}).thenApplyAsync(tasks -> {
			System.out.println("Run by " + Thread.currentThread().getName());
			return tasks.stream().map(callable -> {
				try {
					System.out.println("Run by " + Thread.currentThread().getName());
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


}
