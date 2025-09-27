package com.example.bankcards.util;

import com.example.bankcards.dto.param.CardBlockingRequestSearchParam;
import com.example.bankcards.entity.CardBlockingRequest;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

/**
 * Класс для построения спецификации для поиска запросов на блокировку карт
 */

public class CardBlockingRequestSpecifications {

    public static Specification<CardBlockingRequest> cardBlockingRequestSearchSpec(CardBlockingRequestSearchParam params) {
        return isStatus(params.getSolved())
                .and(createdFrom(params.getCreatedFrom()))
                .and(createdTo(params.getCreatedTo()));
    }
    public static Specification<CardBlockingRequest> isStatus(Boolean solved) {
        if (solved == null) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("solved"), solved);
    }

    public static Specification<CardBlockingRequest> createdFrom(LocalDate createdFrom) {
        if (createdFrom == null) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("created"), createdFrom);
    }

    public static Specification<CardBlockingRequest> createdTo(LocalDate createdTo) {
        if (createdTo == null) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("created"), createdTo.atTime(23, 59, 59, 999_999_999));
    }
}
