package com.tianxiaohui.expt.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MultiLevelTasksOneThreadPool {

	// 一个最多只有4个线程的线程池
	private static final ExecutorService es = Executors.newFixedThreadPool(4);

	public static void main(String[] args) {
		List<Future<String>> futures = new ArrayList<Future<String>>();
		for (int i = 0; i < 8; i++) {
			futures.add(es.submit(new Callable<String>() {// 第一层任务扔到线程池
				@Override
				public String call() throws Exception {
					System.out.println("In level 1 task: " + Thread.currentThread().getName());
					//部署第二层任务
					Future<Long> curTimeFuture = doLevel2Tasks();
					//等第二层任务结果
					return curTimeFuture.get().toString();
				}
			}));
		}
		
		for (Future<String> future : futures) {//等所有结果结束
			try {
				System.out.println(future.get());
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
	}

	public static Future<Long> doLevel2Tasks() {
		return es.submit(new Callable<Long>() {// 第二层任务也扔到了同一个线程池
			@Override
			public Long call() throws Exception {
				System.out.println("In level 2 task: " + Thread.currentThread().getName());
				return System.currentTimeMillis();
			}
		});
	}
}
