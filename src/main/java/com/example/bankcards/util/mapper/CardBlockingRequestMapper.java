package com.example.bankcards.util.mapper;

import com.example.bankcards.dto.CardBlockingRequestDto;
import com.example.bankcards.dto.CardBlockingRequestFullDto;
import com.example.bankcards.entity.CardBlockingRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CardBlockingRequestMapper {
    CardBlockingRequestDto toDto(CardBlockingRequest request);

    List<CardBlockingRequestFullDto> toDto(List<CardBlockingRequest> requests);
}
