package com.example.bankcards.util;

import com.example.bankcards.dto.param.CardSearchParam;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Класс для построения спецификации для поиска карт
 */
public class CardSpecifications {
    public static Specification<Card> cardSearchSpec(CardSearchParam param) {
        return cardNumberLike(param.getNumber())
                .and(userIdIs(param.getUserId()))
                .and(cardStatusIs(param.getStatus()))
                .and(createdIs(param.getCreated()))
                .and(expirationIs(param.getExpiration()));
    }

    public static Specification<Card> cardNumberLike(String number) {
        if (number == null || number.isEmpty()) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("number"), "%" + number + "%");
    }

    public static Specification<Card> userIdIs(Long userId) {
        if (userId == null) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("user").get("id"), userId);
    }

    public static Specification<Card> cardStatusIs(CardStatus status) {
        if (status == null) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<Card> createdIs(LocalDate created) {
        if (created == null) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
        }
        return (root, query, criteriaBuilder) -> {
            LocalDateTime startOfDay = created.atStartOfDay();
            LocalDateTime endOfDay = created.atTime(23, 59, 59, 999_999_999);
            return criteriaBuilder.between(root.get("created"), startOfDay, endOfDay);
        };
    }

    public static Specification<Card> expirationIs(LocalDate expiration) {
        if (expiration == null) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("expiration"), expiration);
    }
}
