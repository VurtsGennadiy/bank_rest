package com.example.bankcards.repository;

import com.example.bankcards.entity.CardBlockingRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CardBlockingRequestRepository extends JpaRepository<CardBlockingRequest, Long> {
    Page<CardBlockingRequest> findAll(Specification<CardBlockingRequest> specification, Pageable pageable);
}
