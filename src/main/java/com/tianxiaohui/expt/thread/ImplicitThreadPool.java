package com.tianxiaohui.expt.thread;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class ImplicitThreadPool {

	public static void main(String[] args) {
		List<String> names = Arrays.asList("Daniel", "Barry", "Eric", "Yuan");

		// 相当于 for loop. 这里都是当前 main 函数的主线程来执行这些 task. 不包括 call 里面的
		List<Callable<String>> tasksList0 = names.stream().map(name -> {
			System.out.println("Before make callable task, run by " + Thread.currentThread().getName());
			return new Callable<String>() {
				public String call() throws Exception {
					System.out.println("In callable task0, run by " + Thread.currentThread().getName());
					return name.toUpperCase();//名字变大写
				}
			};
		}).collect(Collectors.toList());

		// 多线程合作 做这点事. 由 ForkJoinPool.commonPool-worker 线程池执行
		List<Callable<String>> tasksList1 = names.parallelStream().map(name -> {
			System.out.println("Before make callable task, in parallelStream, run by " + Thread.currentThread().getName());
			return new Callable<String>() {
				public String call() throws Exception {
					System.out.println("In callable task1, run by " + Thread.currentThread().getName());
					return name.toUpperCase();
				}
			};
		}).collect(Collectors.toList());

		
		List<String> strList = CompletableFuture.supplyAsync(() -> {
			System.out.println("0 - Run by " + Thread.currentThread().getName());
			return tasksList0;
		}).thenApplyAsync(tasks -> {//这里的 tasks 并没有用到, 是第一步返回的 tasksList0
			System.out.println("1 - Run by " + Thread.currentThread().getName());
			return tasks.stream().map(callable -> {
				System.out.println("2 - Run by " + Thread.currentThread().getName());
				try {
					System.out.println("3 - Run by " + Thread.currentThread().getName());
					return callable.call();//名字变大写的任务被执行
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			});
		}).exceptionally(e -> {
			e.printStackTrace();
			return null;
		}).join().collect(Collectors.toList());
		
		//最早的名字变成大写之后, 这里输出
		strList.stream().forEach(str -> System.out.println(str));
		
		//到此为止 tasksList0 & tasksList1 Callable task 全部没有被执行
		try {
			//tasksList1 会被 ForkJoinPool-X-worker-M 执行
			Executors.newWorkStealingPool().invokeAll(tasksList1);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}

	
}
