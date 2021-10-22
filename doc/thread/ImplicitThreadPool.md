# ImplicitThreadPool

这里涉及3个暗含的线程池
1. Stream 的 parallelStream 默认使用 ForkJoinPool.commonPool 线程池. Stream 的非并行 stream 默认使用当前线程, 类似 for 循环的方式;
2. CompletableFuture.supplyAsync() 方法默认也是用 ForkJoinPool.commonPool 线程池;
3. Executors.newWorkStealingPool() 使用的是创建的 ForkJoinPool-X-worker-M 线程池;

其中 ForkJoinPool.commonPool 是根据当前可运行的 CPU 个数相关的;
另外 Executors.newWorkStealingPool() 创建的 ForkJoinPool-X-worker-M 线程池, 即使不主动 shutdown() 或 shutdownNow(), 它也会随着长时间没人用线程而消亡. 