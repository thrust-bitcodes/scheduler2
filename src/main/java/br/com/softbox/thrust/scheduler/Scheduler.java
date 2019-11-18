package br.com.softbox.thrust.scheduler;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import br.com.softbox.thrust.api.thread.LocalWorkerThreadPool;

public class Scheduler {

	private final LocalWorkerThreadPool pool;
	private final List<SchedulerWorkerThread> threads = new ArrayList<>();

	private static Scheduler scheduler;

	private Scheduler(int minThreads, int maxThreads, String rootPath) throws Exception {
		this.pool = new LocalWorkerThreadPool(minThreads, maxThreads, rootPath, (currentPool) -> build(currentPool));
	}

	public static synchronized Scheduler initScheduler(int minThreads, int maxThread, String rootPath)
			throws Exception {
		if (scheduler != null) {
			throw new RuntimeException("Scheduler was already initiated");
		}
		scheduler = new Scheduler(minThreads, maxThread, rootPath);
		return scheduler;
	}

	public static synchronized Scheduler getScheduler() {
		return scheduler;
	}

	public void simpleSchedule(String script, int time, boolean startNow) {
		SchedulerWorkerThread worker = getSchedulerWorker();
		worker.simpleSchedule(script, time, startNow);
		this.threads.add(worker);
	}

	public void timeSchedule(String script, String[] time, boolean now) {
		SchedulerWorkerThread worker = getSchedulerWorker();
		worker.timeSchedule(script, time, now);
		this.threads.add(worker);
	}

	private SchedulerWorkerThread getSchedulerWorker() {
		SchedulerWorkerThread worker = (SchedulerWorkerThread) pool.getThrustWorkerThread();
		if (worker == null) {
			throw new RuntimeException("No more workers for scheduler");
		}
		return worker;
	}

	private static SchedulerWorkerThread build(LocalWorkerThreadPool thePool) throws IOException, URISyntaxException {
		return new SchedulerWorkerThread(thePool);
	}

	public void cancel() {
		this.threads.forEach(this::cancelWorker);
		this.removeNotAliveThreads();
	}

	private void cancelWorker(SchedulerWorkerThread worker) {
		if (worker.isAlive() && !worker.isInterrupted()) {
			try {
				worker.inactivate();
				worker.interrupt();
			} catch (Exception e) {
				System.err.println("Failed to interrupt worker " + worker.getName() + ": " + e.getMessage());
			}
		}
	}

	public void waitSchedulers(Integer time) {
		int waitTime = time == null ? 60000 : time.intValue();
		boolean waiting = true;
		while (waiting) {
			removeNotAliveThreads();
			waiting = !this.threads.isEmpty();
			if (waiting) {
				try {
					Thread.sleep(waitTime);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					System.err.println("Scheduler was interrupted: " + e.getMessage());
					waiting = false;
				}
			}
		}
	}

	private synchronized void removeNotAliveThreads() {
		for (Iterator<? extends Thread> it = threads.iterator(); it.hasNext();) {
			Thread t = it.next();
			if (!t.isAlive() || t.isInterrupted()) {
				it.remove();
			}
		}
	}

}
