package com.tianxiaohui.expt.thread;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class ForkJoinPool {
	public static void main(String[] args) {
		ForkJoinPool.testExecutorsNewWorkStealingPool();
	}

	public static void testExecutorsNewWorkStealingPool() {
		List<Callable<String>> tasks = Arrays.asList("Daniel", "Barry", "Eric", "Yuan").stream().map(name -> {
			return new Callable<String>() {
				public String call() throws Exception {
					System.out.println("Run by " + Thread.currentThread().getName());
					return name.toUpperCase();
				}
			};
		}).collect(Collectors.toList());
		
		try {
			Executors.newWorkStealingPool().invokeAll(tasks);
			Executors.newWorkStealingPool().invokeAll(tasks);
			Executors.newWorkStealingPool().invokeAll(tasks);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		try {
			Thread.sleep(360_000L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
