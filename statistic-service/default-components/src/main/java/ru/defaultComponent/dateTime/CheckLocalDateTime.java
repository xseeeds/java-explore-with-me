package ru.defaultComponent.dateTime;

import lombok.experimental.UtilityClass;
import ru.defaultComponent.exception.exp.BadRequestException;
import java.time.LocalDateTime;

@UtilityClass
public class CheckLocalDateTime {

    public void checkStartIsAfterEndMayBeNull(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return;
        }
        if (!start.isBefore(end)) {
            throw new BadRequestException("PUBLIC => Время начала => " + start + " не может быть позже времени окончания => " + end);
        }
    }

    public void checkStartIsAfterEndNotBeNull(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            throw new BadRequestException("PUBLIC => Время начала и окончания не должна быть null");
        }
        if (!start.isBefore(end)) {
            throw new BadRequestException("PUBLIC => Время начала => " + start + " не может быть позже времени окончания => " + end);
        }
    }

    public void checkEventDateToUpdateEventAdmin(LocalDateTime eventDate) {
        if (eventDate != null && !eventDate.isAfter(LocalDateTime.now().plusHours(1))) {
            throw new BadRequestException("ADMIN => Нельзя внести изменения в течении часа до его начала");
        }
    }

    public void checkEventDateToAddEventPrivate(LocalDateTime eventDate) {
        if (eventDate == null || !eventDate.isAfter(LocalDateTime.now().plusHours(2))) {
            throw new BadRequestException("PRIVATE => Событие не может начинаться в ближайшие два часа");
        }
    }

    public void checkEventDateToUpdateEventPrivate(LocalDateTime eventDate) {
        if (eventDate != null && !eventDate.isAfter(LocalDateTime.now().plusHours(2))) {
            throw new BadRequestException("PRIVATE => Нельзя внести изменения в течении двух часов до его начала");
        }
    }

}