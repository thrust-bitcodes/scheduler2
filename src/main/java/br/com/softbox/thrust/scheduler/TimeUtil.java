package br.com.softbox.thrust.scheduler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class TimeUtil {

	private TimeUtil() {
		super();
	}

	public static List<LocalDateTime> toLocalDateTime(List<String> times) {
		return times.stream().map(TimeUtil::asLocalDateTime).collect(Collectors.toList());
	}

	public static List<LocalDateTime> addNextDayToList(List<LocalDateTime> list) {
		List<LocalDateTime> nextDayList = list.stream().map(a -> a).collect(Collectors.toList());
		nextDayList.addAll(list);
		return nextDayList;
	}

	public static LocalDateTime minorNextFrom(List<LocalDateTime> list, LocalDateTime baseLocalDateTime) {
		Optional<LocalDateTime> opt = list.stream().filter(d -> !d.isBefore(baseLocalDateTime)).sorted().findFirst();
		return opt.get();
	}

	public static LocalDateTime minortNextIncludeNextDay(List<LocalDateTime> list, LocalDateTime baseLocalDateTime) {
		return minorNextFrom(addNextDayToList(list), baseLocalDateTime);
	}

	public static long calculateMinorTimeFromNow(List<String> timesList) {

		LocalDateTime now = LocalDateTime.now();
		List<LocalDateTime> times = toLocalDateTime(timesList);

		LocalDateTime minor = minortNextIncludeNextDay(times, now);
		return now.until(minor, ChronoUnit.MILLIS);
	}

	public static LocalDateTime asLocalDateTime(String hourMinuteSeconds) {
		try (Scanner scanner = new Scanner(hourMinuteSeconds)) {
			scanner.useDelimiter(":");
			int h = scanner.nextInt();
			int m = scanner.nextInt();
			int s = scanner.nextInt();

			LocalDate date = LocalDate.now();
			return LocalDateTime.of(date, LocalTime.of(h, m, s));
		}
	}

}
