package com.example.bankcards.util.specification;

import com.example.bankcards.dto.filters.UserSearchParam;
import com.example.bankcards.entity.User;
import org.springframework.data.jpa.domain.Specification;

/**
 * Класс для построения спецификации для поиска пользователей
 */

public class UserSpecifications {
    public static Specification<User> userSearchSpec(UserSearchParam param) {
        return nameLike(param.getUsername()).and(emailLike(param.getEmail()));
    }

    public static Specification<User> nameLike(String username) {
        if (username == null || username.isEmpty()) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("username"), "%" + username + "%");
    }


    public static Specification<User> emailLike(String email) {
        if (email == null || email.isEmpty()) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("email"), "%" + email + "%");
    }
}
