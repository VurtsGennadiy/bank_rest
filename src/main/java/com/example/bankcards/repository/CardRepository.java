package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, String> {
    List<Card> findAllByOwnerId(Long ownerId);

    Page<Card> findAll(Specification<Card> specification, Pageable pageable);

    @Query("SELECT c FROM Card c JOIN FETCH c.owner WHERE c.number = :number")
    Optional<Card> findByNumberJoinOwner(String number);

    Optional<Card> findCardByOwner_IdAndNumberContaining(Long ownerId, String number);
}
