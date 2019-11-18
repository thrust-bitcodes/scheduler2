package br.com.softbox.thrust.scheduler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

public class TimeUtilTest {

	private static void assertTime223311(LocalDateTime value) {
		Assert.assertNotNull(value);
		LocalDateTime expected = LocalDateTime.of(LocalDate.now(), LocalTime.of(22, 33, 11));
		Assert.assertEquals(value, expected);
	}

	@Test
	public void testAsLocalDateTimeSuccess() {
		assertTime223311(TimeUtil.asLocalDateTime("22:33:11"));
	}

	@Test(expected = NullPointerException.class)
	public void testAsLocalDateTimeNullString() {
		TimeUtil.asLocalDateTime(null);
	}

	@Test(expected = NoSuchElementException.class)
	public void testAsLocalDateTimeNoString() {
		TimeUtil.asLocalDateTime("");
	}

	@Test(expected = NoSuchElementException.class)
	public void testAsLocalDateTimeMissingMinutesAndSeconds() {
		TimeUtil.asLocalDateTime("23");
	}

	@Test
	public void listStringToLocalDateTime() {
		List<String> source = Arrays.asList("22:33:11");
		List<LocalDateTime> dest = TimeUtil.toLocalDateTime(source);
		Assert.assertNotNull(dest);
		Assert.assertFalse(dest.isEmpty());
		Assert.assertEquals(1, dest.size());

		assertTime223311(dest.get(0));
	}

	@Test
	public void listStringToLocalDateTimeEmpty() {
		List<String> source = new ArrayList<>();
		List<LocalDateTime> dest = TimeUtil.toLocalDateTime(source);
		Assert.assertNotNull(dest);
		Assert.assertTrue(dest.isEmpty());
	}

	@Test
	public void testMinorTimeFromMinutes() {

		LocalDateTime now = LocalDateTime.now();
		LocalDateTime before = now.minusMinutes(2);
		LocalDateTime after1 = now.plusMinutes(2);
		LocalDateTime after2 = after1.plusMinutes(3);

		LocalDateTime minor = TimeUtil.minortNextIncludeNextDay(Arrays.asList(before, after1, after2), now);
		Assert.assertNotNull(minor);
		Assert.assertEquals(minor, after1);

		minor = TimeUtil.minortNextIncludeNextDay(Arrays.asList(now, after1, after2), before);
		Assert.assertNotNull(minor);
		Assert.assertEquals(minor, now);

		minor = TimeUtil.minortNextIncludeNextDay(Arrays.asList(now, before, after2), after1);
		Assert.assertNotNull(minor);
		Assert.assertEquals(minor, after2);
	}

	@Test
	public void testCalculateNextSleepTimeManual() {

		LocalDateTime now = LocalDateTime.now();
		LocalDateTime before = now.minusMinutes(2);
		LocalDateTime after1 = now.plusMinutes(2);
		LocalDateTime after2 = after1.plusMinutes(3);
		
		LocalDateTime minor = TimeUtil.minortNextIncludeNextDay(Arrays.asList(before, after1, after2), now);
		Assert.assertNotNull(minor);
		Assert.assertEquals(minor, after1);

		long diffTime = now.until(minor, ChronoUnit.MILLIS);
		Assert.assertEquals(diffTime, 120_000);
	}

	@Test
	public void testCalculateMinorTimeFromNow() {

		LocalDateTime now = LocalDateTime.now();
		LocalDateTime before = now.minusMinutes(2);
		LocalDateTime after1 = now.plusMinutes(2);
		LocalDateTime after2 = after1.plusMinutes(3);
		
		List<String> timesList = Arrays.asList(before, after1, after2).stream()
				.map((d) -> String.format("%02d:%02d:00", d.getHour(), d.getMinute())).collect(Collectors.toList());

		long diffFromNow = TimeUtil.calculateMinorTimeFromNow(timesList);
		Assert.assertTrue(diffFromNow >= 50_000);
		Assert.assertTrue(diffFromNow <= 130_000);
	}

}