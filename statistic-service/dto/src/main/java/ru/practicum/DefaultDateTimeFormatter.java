package ru.practicum;

import lombok.experimental.UtilityClass;

import java.time.format.DateTimeFormatter;

@UtilityClass
public class DefaultDateTimeFormatter {

    public static final String PATTERN_DATE_TIME = "yyyy-MM-dd HH:mm:ss";

    public DateTimeFormatter getDefaultDateTimeFormatter() {
        return DateTimeFormatter.ofPattern(PATTERN_DATE_TIME);
    }

}
