package com.example.bankcards.util;

import com.example.bankcards.dto.CardShortDto;
import com.example.bankcards.entity.Card;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

/**
 * Маппинг между сущностями и DTO для card
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CardMapper {

    CardShortDto toShortDto(Card card);
}
