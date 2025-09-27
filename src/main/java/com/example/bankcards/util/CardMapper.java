package com.example.bankcards.util;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.CardFullDto;
import com.example.bankcards.entity.Card;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

/**
 * Маппинг между сущностями и DTO для card
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CardMapper {

    @Mapping(target = "number", source = "maskedNumber")
    @Mapping(target = "ownerId", source = "card.owner.id")
    CardDto toCardDto(Card card, String maskedNumber);

    @Mapping(target = "ownerId", source = "card.owner.id")
    CardDto toCardDto(Card card);

    List<CardDto> toCardDto(List<Card> cards);

    @Mapping(target = "number", source = "maskedNumber")
    CardFullDto toCardFullDto(Card card, String maskedNumber);
}
