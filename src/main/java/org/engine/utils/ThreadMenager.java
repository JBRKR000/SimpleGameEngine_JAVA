package org.engine.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadMenager {
    private final ExecutorService workerPool;
    private final Queue<Runnable> mainThreadQueue = new ConcurrentLinkedQueue<>();
    private final List<Thread> longRunningThreads = new ArrayList<>();

    public ThreadMenager() {
        this.workerPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    public void submitToWorker(Runnable task) {
        workerPool.submit(task);
    }

    public void submitToMain(Runnable task) {
        mainThreadQueue.add(task);
    }

    public void updateMainThreadQueue() {
        while (!mainThreadQueue.isEmpty()) {
            mainThreadQueue.poll().run();
        }
    }

    public void addLongRunningThread(Thread thread) {
        longRunningThreads.add(thread);
        thread.start();
    }

    public void shutdown() {
        workerPool.shutdownNow();
        for (Thread t : longRunningThreads) {
            t.interrupt();
        }
    }

    public String getMainThreadQueueSize() {
        return String.valueOf(mainThreadQueue.size());
    }
}
