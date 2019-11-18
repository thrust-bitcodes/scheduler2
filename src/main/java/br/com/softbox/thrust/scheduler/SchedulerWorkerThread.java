package br.com.softbox.thrust.scheduler;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.graalvm.polyglot.Value;

import br.com.softbox.thrust.api.thread.LocalWorkerThreadPool;
import br.com.softbox.thrust.api.thread.ThrustWorkerThread;

public class SchedulerWorkerThread extends ThrustWorkerThread {

	private static final String SCHEDULER_BITCODE_PATH = "thrust-bitcodes/scheduler2";
	private static final List<String> JS_FILES = Arrays.asList("task.js");

	private Value taskJS;
	private String scriptPath;

	private boolean startNow;
	private Long sleepTime;
	private List<String> times;

	public SchedulerWorkerThread(LocalWorkerThreadPool pool) throws IOException, URISyntaxException {
		super(pool, SCHEDULER_BITCODE_PATH, JS_FILES);
		this.taskJS = this.listJS.get(0);
	}

	@Override
	public void run() {
		while (active.get()) {
			if (!startNow) {
				try {
					long time = this.sleepTime != null ? this.sleepTime : calculateNextTime();
					if (time > 0) {
						sleep(time);
					}
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					inactivate();
					break;
				}
			} else {
				startNow = false;
			}
			if (active.get()) {
				try {
					taskJS.invokeMember("runScheduleTask", scriptPath, this);
				} catch (Exception e) {
					System.err.println("SchedulerWorkerThread error: " + e.getMessage());
				}
			}
		}
	}

	private long calculateNextTime() {
		long time = TimeUtil.calculateMinorTimeFromNow(this.times);
		return active.get() ? time : 0;
	}

	public void cancel() {
		this.inactivate();
	}

	private void runThisThread() {
		if (!isAlive()) {
			this.start();
		} else {
			this.notify();
		}
	}

	private void setScript(String script) {
		this.scriptPath = script;
		Path scriptPath = Paths.get(script);
		if (!Files.exists(scriptPath)) {
			Path relativePath = Paths.get(this.pool.getRootPath(), script);
			if (Files.exists(relativePath)) {
				this.scriptPath = relativePath.toAbsolutePath().toString();
			} else {
				throw new RuntimeException("Script not found: " + script);
			}
		}
	}

	public void simpleSchedule(String script, long time, boolean now) {
		setScript(script);
		this.times = null;
		this.sleepTime = time;
		this.startNow = now;
		runThisThread();
	}

	public void timeSchedule(String script, String[] timesStr, boolean startNow) {
		setScript(script);
		this.sleepTime = null;
		this.startNow = startNow;
		this.times = Arrays.stream(timesStr).map(SchedulerWorkerThread::adjustTime).collect(Collectors.toList());
		runThisThread();
	}

	private static String adjustTime(String time) {

		String[] timesArray = time.split(":");

		String hour = timesArray[0];
		String minute = timesArray.length > 1 ? timesArray[1] : "0";
		String seconds = timesArray.length > 2 ? timesArray[2] : "0";

		hour = validateTimeInteger(hour, 23, time);
		minute = validateTimeInteger(minute, 59, time);
		seconds = validateTimeInteger(seconds, 59, time);

		return String.format("%s:%s:%s", hour, minute, seconds);
	}

	private static String validateTimeInteger(String t, int max, String src) {
		int time;
		try {
			time = Integer.parseInt(t);
		} catch (NumberFormatException e) {
			throw new RuntimeException("Invalid time: " + src, e);
		}
		if (time > max) {
			throw new RuntimeException("Invalid time (max): " + src);
		}
		return String.format("%02d", time);
	}

}