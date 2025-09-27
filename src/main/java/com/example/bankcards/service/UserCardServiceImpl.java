package com.example.bankcards.service;

import com.example.bankcards.dto.CardBlockingRequestDto;
import com.example.bankcards.dto.CardShortDto;
import com.example.bankcards.dto.param.CardSearchParam;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardBlockingRequest;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.repository.CardBlockingRequestRepository;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.util.CardBlockingRequestMapper;
import com.example.bankcards.util.CardMapper;
import com.example.bankcards.util.CardNumberMasker;
import com.example.bankcards.util.CardSpecifications;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserCardServiceImpl implements UserCardService {
    private final CardRepository cardRepository;
    private final CardBlockingRequestRepository cardBlockingRequestRepository;
    private final CardMapper cardMapper;
    private final CardBlockingRequestMapper cardBlockingRequestMapper;

    @Override
    public List<CardShortDto> getCards(CardSearchParam params) {
        log.trace("Request to get user cards {}", params);
        Pageable page = PageRequest.of(params.getFrom() / params.getSize(), params.getSize());
        List<Card> cards = cardRepository.findAll(CardSpecifications.cardSearchSpec(params), page).getContent();
        cards.forEach(card -> card.setNumber(CardNumberMasker.mask(card.getNumber())));
        return cardMapper.toCardShortDto(cards);
    }

    @Override
    @Transactional
    public CardBlockingRequestDto createCardBlockingRequest(Long userId, String partCardNumber) {
        log.trace("Request to blocking card '{}' from user with id '{}'", partCardNumber, userId);
        Card card = cardRepository.findCardByOwner_IdAndNumberContaining(userId, partCardNumber)
                .orElseThrow(() -> new NotFoundException(String.format("Card with number %s not found", partCardNumber)));
        CardBlockingRequest blockingRequest = new CardBlockingRequest();
        blockingRequest.setCardNumber(card.getNumber());
        blockingRequest.setUser(card.getOwner());
        cardBlockingRequestRepository.save(blockingRequest);

        CardBlockingRequestDto dto = cardBlockingRequestMapper.toDto(blockingRequest);
        String maskedCardNumber = CardNumberMasker.mask(card.getNumber());
        dto.setCardNumber(maskedCardNumber);
        log.info("Request to blocking card '{}' by user '{}' was created at '{}'",
                maskedCardNumber, userId, blockingRequest.getCreated());
        return dto;
    }
}
