package br.com.softbox.thrust.scheduler;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import br.com.softbox.thrust.core.Thrust;

public class SchedulerTest {
	private static final Path bitcodeSrcPath = Paths.get("lib");
	private static final Path rootPath = Paths.get("./src/test/thrust");
	private static final Path libPath = rootPath.resolve(".lib");
	private static final Path bitcodePath = libPath.resolve("bitcodes").resolve("thrust-bitcodes").resolve("scheduler2");

	@BeforeClass
	public static void prepareDirectory() throws IOException {
		rmdir();
		buildBitcodeDirectory();
	}
	
	@Before
	public void clearScheduler() throws Exception {
		if (Scheduler.getScheduler() != null) {
			Class<?> clazz = Scheduler.class;
			Field instanceField = clazz.getDeclaredField("scheduler");
			Assert.assertNotNull(instanceField);
			instanceField.setAccessible(true);
			instanceField.set(null, null);
			Assert.assertNull(Scheduler.getScheduler());
		}
	}

	@AfterClass
	public static void clearDirectory() throws IOException {
		rmdir();
	}

	private static void rmdir() throws IOException {
		if (Files.exists(libPath)) {
			Files.walk(libPath).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
		}
	}

	private static void buildBitcodeDirectory() throws IOException {
		File bitcodeDirFile = bitcodePath.toFile();
		if (!bitcodeDirFile.exists() && !bitcodeDirFile.mkdirs()) {
			throw new RuntimeException("Failed to create: " + bitcodeDirFile);
		}
		for (String file : Arrays.asList("index.js", "task.js")) {
			Path src = bitcodeSrcPath.resolve(file);
			Path dst = bitcodePath.resolve(file);
			Files.copy(src, dst);
		}
	}

	@Test(timeout = 61000)
	public void testTask01Wait() throws Exception {
		String file = "src/test/thrust/schedule-task01-wait.js";
		Thrust.main(new String[] { file });
	}
	
	@Test(timeout = 20001)
	public void testTask01Now() throws Exception {
		String file = "src/test/thrust/schedule-task01-now.js";
		Thrust.main(new String[] { file });
	}
	
	@Test(timeout = 61000)
	public void testTask02FiveTimes() throws Exception {
		String file = "src/test/thrust/schedule-task02-run-5-times.js";
		Thrust.main(new String[] { file });
	}
	
	@Test(timeout = 3000)
	public void testTask02Cancel() throws Exception {
		String file = "src/test/thrust/schedule-task02-no-run.js";
		Thrust.main(new String[] { file });
	}
	
	@Test(timeout = 90000)
	public void testTask03Run2Times() throws Exception {
		String file = "src/test/thrust/schedule-task03-run-2-times.js";
		Thrust.main(new String[] { file });
	}

}
