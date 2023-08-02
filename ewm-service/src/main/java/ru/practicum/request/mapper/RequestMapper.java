package ru.practicum.request.mapper;

import lombok.experimental.UtilityClass;
import ru.defaultComponent.ewmService.dto.request.ParticipationResponseDto;
import ru.practicum.request.model.ParticipationEntity;

@UtilityClass
public class RequestMapper {

    public ParticipationResponseDto toParticipationResponseDto(ParticipationEntity participationEntity) {
        return ParticipationResponseDto.builder()
                .id(participationEntity.getId())
                .created(participationEntity.getCreatedOn())
                .event(participationEntity.getEvent())
                .requester(participationEntity.getRequester())
                .status(participationEntity.getStatus())
                .build();
    }

}
