package ru.practicum.participation.mapper;

import lombok.experimental.UtilityClass;
import ru.defaultComponent.ewmService.dto.participation.ParticipationResponseDto;
import ru.practicum.participation.model.ParticipationEntity;

@UtilityClass
public class ParticipationMapper {

    public ParticipationResponseDto toParticipationResponseDto(ParticipationEntity participationEntity) {
        return ParticipationResponseDto.builder()
                .id(participationEntity.getId())
                .created(participationEntity.getCreatedOn())
                .event(participationEntity.getEvent())
                .requester(participationEntity.getRequester())
                .status(participationEntity.getState())
                .build();
    }

}
