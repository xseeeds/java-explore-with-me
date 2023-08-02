package ru.defaultComponent.dateTime;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class DefaultDateTimeFormatter {

    public static final String PATTERN_DATE_TIME = "yyyy-MM-dd HH:mm:ss";

    public DateTimeFormatter getDefaultDateTimeFormatter() {
        return DateTimeFormatter.ofPattern(PATTERN_DATE_TIME);
    }

    public String getStringFormattingLocalDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.format(getDefaultDateTimeFormatter());
    }

    public LocalDateTime getLocalDateTimeFormatting(String string) {
        if (string == null) {
            return null;
        }
        return LocalDateTime.parse(string, getDefaultDateTimeFormatter());
    }

}
