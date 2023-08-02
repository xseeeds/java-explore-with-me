package ru.practicum.event.mapper;

import lombok.experimental.UtilityClass;
import ru.defaultComponent.ewmService.dto.event.EventFullDto;
import ru.defaultComponent.ewmService.dto.event.EventShortDto;
import ru.defaultComponent.ewmService.dto.event.LocationDto;
import ru.defaultComponent.ewmService.dto.event.NewEventDto;
import ru.practicum.category.model.CategoryEntity;
import ru.practicum.event.model.EventEntity;
import ru.practicum.category.mapper.CategoryMapper;
import ru.defaultComponent.ewmService.enums.EventState;
import ru.practicum.event.model.Location;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.UserEntity;

import java.time.LocalDateTime;

import static ru.defaultComponent.dateTime.DefaultDateTimeFormatter.getLocalDateTimeFormatting;

@UtilityClass
public class EventMapper {

    public EventEntity toEventEntity(NewEventDto newEventDto, UserEntity userEntity, CategoryEntity categoryEntity) {
        return EventEntity
                .builder()
                .annotation(newEventDto.getAnnotation())
                .categoryEntity(categoryEntity)
                .category(categoryEntity.getId())
                .confirmedRequests(0)
                .createdOn(LocalDateTime.now())
                .description(newEventDto.getDescription())
                .eventDate(getLocalDateTimeFormatting(newEventDto.getEventDate()))
                .initiatorEntity(userEntity)
                .initiator(userEntity.getId())
                .location(toLocation(newEventDto.getLocation()))
                .paid(newEventDto.getPaid())
                .participantLimit(newEventDto.getParticipantLimit())
                .publishedOn(LocalDateTime.now())
                .requestModeration(newEventDto.getRequestModeration())
                .state(EventState.PENDING)
                .title(newEventDto.getTitle())
                .views(0)
                .build();
    }

    public EventFullDto toEventFullDto(EventEntity eventEntity) {
        return EventFullDto
                .builder()
                .id(eventEntity.getId())
                .annotation(eventEntity.getAnnotation())
                .category(CategoryMapper.toCategoryDto(eventEntity.getCategoryEntity()))
                .confirmedRequests(eventEntity.getConfirmedRequests())
                .createdOn(eventEntity.getCreatedOn())
                .description(eventEntity.getDescription())
                .eventDate(eventEntity.getEventDate())
                .initiator(UserMapper.toUserShortDto(eventEntity.getInitiatorEntity()))
                .location(toLocationDto(eventEntity.getLocation()))
                .paid(eventEntity.getPaid())
                .participantLimit(eventEntity.getParticipantLimit())
                .publishedOn(eventEntity.getPublishedOn())
                .requestModeration(eventEntity.getRequestModeration())
                .state(eventEntity.getState())
                .title(eventEntity.getTitle())
                .views(eventEntity.getViews())
                .build();
    }

    public EventShortDto toEventShortDto(EventEntity eventEntity) {
        return EventShortDto
                .builder()
                .annotation(eventEntity.getAnnotation())
                .category(CategoryMapper.toCategoryDto(eventEntity.getCategoryEntity()))
                .confirmedRequests(eventEntity.getConfirmedRequests())
                .eventDate(eventEntity.getEventDate())
                .id(eventEntity.getId())
                .initiator(UserMapper.toUserShortDto(eventEntity.getInitiatorEntity()))
                .paid(eventEntity.getPaid())
                .title(eventEntity.getTitle())
                .views(eventEntity.getViews())
                .build();
    }

    public Location toLocation(LocationDto locationDto) {
        return Location
                .builder()
                .lat(locationDto.getLat())
                .lon(locationDto.getLon())
                .build();
    }

    public LocationDto toLocationDto(Location location) {
        return LocationDto
                .builder()
                .lat(location.getLat())
                .lon(location.getLon())
                .build();
    }

}
