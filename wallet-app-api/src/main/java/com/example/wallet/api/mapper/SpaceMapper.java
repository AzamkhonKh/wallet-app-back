package com.example.wallet.api.mapper;

import com.example.wallet.api.dto.SpaceDto;
import com.example.wallet.core.domain.Space;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE) // Static methods only
public class SpaceMapper {

    public static SpaceDto toDto(Space space) {
        if (space == null) {
            return null;
        }
        return new SpaceDto(
                space.getId(),
                space.getName(),
                space.getDescription(),
                space.getCurrency(),
                space.getCreatedAt(),
                space.getUpdatedAt()
        );
    }

    public static List<SpaceDto> toDtoList(List<Space> spaces) {
        if (spaces == null) {
            return List.of();
        }
        return spaces.stream()
                .map(SpaceMapper::toDto)
                .collect(Collectors.toList());
    }
}