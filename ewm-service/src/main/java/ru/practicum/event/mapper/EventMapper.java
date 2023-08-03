package ru.practicum.event.mapper;

import lombok.experimental.UtilityClass;
import ru.defaultComponent.ewmService.dto.event.*;
import ru.defaultComponent.statisticServer.dto.StatisticRequest;
import ru.practicum.category.model.CategoryEntity;
import ru.practicum.event.model.EventEntity;
import ru.practicum.category.mapper.CategoryMapper;
import ru.defaultComponent.ewmService.enums.EventState;
import ru.practicum.event.model.Location;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.UserEntity;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@UtilityClass
public class EventMapper {

    public EventEntity toNewEventEntity(CreateEventRequestDto createEventRequestDto, UserEntity userEntity, CategoryEntity categoryEntity) {
        return EventEntity
                .builder()
                .annotation(createEventRequestDto.getAnnotation())
                .categoryEntity(categoryEntity)
                .category(categoryEntity.getId())
                .createdOn(LocalDateTime.now())
                .description(createEventRequestDto.getDescription())
                .eventDate(createEventRequestDto.getEventDate())
                .initiatorEntity(userEntity)
                .initiator(userEntity.getId())
                .location(toLocation(createEventRequestDto.getLocation()))
                .paid(createEventRequestDto.getPaid())
                .participantLimit(createEventRequestDto.getParticipantLimit())
                .publishedOn(LocalDateTime.now())
                .requestModeration(createEventRequestDto.getRequestModeration())
                .state(EventState.PENDING)
                .title(createEventRequestDto.getTitle())
                .build();
    }

    public EventFullResponseDto toEventFullResponseDto(EventEntity eventEntity) {
        return EventFullResponseDto
                .builder()
                .id(eventEntity.getId())
                .annotation(eventEntity.getAnnotation())
                .category(CategoryMapper.toCategoryResponseDto(eventEntity.getCategoryEntity()))
                .confirmedRequests(eventEntity.getConfirmedRequests())
                .createdOn(eventEntity.getCreatedOn())
                .description(eventEntity.getDescription())
                .eventDate(eventEntity.getEventDate())
                .initiator(UserMapper.toUserShortResponseDto(eventEntity.getInitiatorEntity()))
                .location(toLocationResponseDto(eventEntity.getLocation()))
                .paid(eventEntity.getPaid())
                .participantLimit(eventEntity.getParticipantLimit())
                .publishedOn(eventEntity.getPublishedOn())
                .requestModeration(eventEntity.getRequestModeration())
                .state(eventEntity.getState())
                .title(eventEntity.getTitle())
                .views(eventEntity.getViews())
                .build();
    }

    public EventShortResponseDto toEventShortResponseDto(EventEntity eventEntity) {
        return EventShortResponseDto
                .builder()
                .annotation(eventEntity.getAnnotation())
                .category(CategoryMapper.toCategoryResponseDto(eventEntity.getCategoryEntity()))
                .confirmedRequests(eventEntity.getConfirmedRequests())
                .eventDate(eventEntity.getEventDate())
                .id(eventEntity.getId())
                .initiator(UserMapper.toUserShortResponseDto(eventEntity.getInitiatorEntity()))
                .paid(eventEntity.getPaid())
                .title(eventEntity.getTitle())
                .views(eventEntity.getViews())
                .build();
    }

    public Location toLocation(LocationRequestDto locationRequestDto) {
        return Location
                .builder()
                .lat(locationRequestDto.getLat())
                .lon(locationRequestDto.getLon())
                .build();
    }

    public LocationResponseDto toLocationResponseDto(Location location) {
        return LocationResponseDto
                .builder()
                .lat(location.getLat())
                .lon(location.getLon())
                .build();
    }

    public StatisticRequest toStatisticRequest(HttpServletRequest httpServletRequest, List<Long> eventsIds) {
        return StatisticRequest
                .builder()
                .app(httpServletRequest.getServerName())
                .uri(httpServletRequest.getRequestURI())
                .eventsIds(eventsIds)
                .ip(httpServletRequest.getRemoteAddr())
                .createdOn(LocalDateTime.now())
                .build();
    }

}
