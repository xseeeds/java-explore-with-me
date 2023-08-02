package ru.practicum.request.mapper;

import lombok.experimental.UtilityClass;
import ru.defaultComponent.ewmService.dto.event.ParticipationRequestDto;
import ru.practicum.request.model.RequestEntity;

@UtilityClass
public class RequestMapper {

    public static ParticipationRequestDto toRequestDto(RequestEntity requestEntity) {
        return ParticipationRequestDto.builder()
                .id(requestEntity.getId())
                .created(requestEntity.getCreatedOn())
                .event(requestEntity.getEvent())
                .requester(requestEntity.getRequester())
                .status(requestEntity.getStatus())
                .build();
    }

}
