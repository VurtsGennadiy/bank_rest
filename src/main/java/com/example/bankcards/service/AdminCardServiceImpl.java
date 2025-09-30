package com.example.bankcards.service;

import com.example.bankcards.dto.CardBlockingRequestFullDto;
import com.example.bankcards.dto.CardCreateRequest;
import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.CardFullDto;
import com.example.bankcards.dto.filters.CardBlockingRequestSearchParam;
import com.example.bankcards.dto.filters.CardSearchParam;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardBlockingRequest;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.CardDeleteException;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.repository.CardBlockingRequestRepository;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.*;
import com.example.bankcards.util.mapper.CardBlockingRequestMapper;
import com.example.bankcards.util.mapper.CardMapper;
import com.example.bankcards.util.specification.CardBlockingRequestSpecifications;
import com.example.bankcards.util.specification.CardSpecifications;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminCardServiceImpl implements AdminCardService {
    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final CardBlockingRequestRepository cardBlockingRequestRepository;
    private final CardMapper cardMapper;
    private final CardBlockingRequestMapper cardBlockingRequestMapper;

    @Override
    @Transactional
    public CardDto createNewCard(CardCreateRequest cardRequest) {
        Long userId = cardRequest.getUserId();
        BigDecimal balance = cardRequest.getBalance();
        log.trace("Request to create new card: {}", cardRequest);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id %s not found", userId)));
        Card card = new Card();

        String cardNumber = CardNumberGenerator.generateMirCardNumber();
        // проверяем, что случайно сгенерированный номер карты не занят
        while (cardRepository.existsById(cardNumber)) {
            cardNumber = CardNumberGenerator.generateMirCardNumber();
        }

        card.setNumber(cardNumber);
        card.setOwner(user);
        card.setExpiration(cardRequest.getExpiration());
        if (balance != null) {
            card.setBalance(balance);
        }

        card = cardRepository.save(card);
        String maskedNumber = CardNumberMasker.mask(card.getNumber());
        log.info("Created new card for user with id: {}, number: '{}', balance: {}", userId, maskedNumber, card.getBalance());
        return cardMapper.toCardDto(card, maskedNumber);
    }

    @Override
    @Transactional
    public CardDto activateCard(String cardNumber) {
        String maskedNumber = CardNumberMasker.mask(cardNumber);
        log.trace("Request to activate card '{}'", maskedNumber);
        Card card = cardRepository.findById(cardNumber)
                .orElseThrow(() -> new NotFoundException(String.format("Card with number %s not found", cardNumber)));
        card.setStatus(CardStatus.ACTIVE);
        log.info("Card '{}' set status ACTIVE", maskedNumber);
        return cardMapper.toCardDto(card, maskedNumber);
    }

    @Override
    @Transactional
    public CardDto blockCardByNumber(String cardNumber) {
        String maskedNumber = CardNumberMasker.mask(cardNumber);
        log.trace("Request to block card number'{}'", maskedNumber);
        Card card = cardRepository.findById(cardNumber)
                .orElseThrow(() -> new NotFoundException(String.format("Card with number %s not found", maskedNumber)));
        card.setStatus(CardStatus.BLOCKED);
        log.info("Card '{}' set status BLOCKED", maskedNumber);
        return cardMapper.toCardDto(card, maskedNumber);
    }

    @Override
    @Transactional
    public CardDto blockCardByRequest(Long cardBlockingRequestId) {
        log.trace("Request to block card id:'{}'", cardBlockingRequestId);
        CardBlockingRequest request = cardBlockingRequestRepository.findById(cardBlockingRequestId)
                .orElseThrow(() -> new NotFoundException(String.format("Card blocking request with id %s not found", cardBlockingRequestId)));
        CardDto response = blockCardByNumber(request.getCardNumber());
        request.setSolved(true);
        log.info("Card blocking request with id {} was solved", cardBlockingRequestId);
        return response;
    }

    @Override
    @Transactional
    public void deleteCard(String cardNumber) {
        String maskedNumber = CardNumberMasker.mask(cardNumber);
        log.trace("Request to delete card '{}'", maskedNumber);
        Card card = cardRepository.findById(cardNumber)
                .orElseThrow(() -> new NotFoundException(String.format("Card with number %s not found", maskedNumber)));
        // запрещено удалять карту с ненулевым балансом
        if (!card.getBalance().equals(BigDecimal.ZERO)) {
            throw new CardDeleteException(String.format("Card with number %s has non-zero balance", maskedNumber));
        }
        cardRepository.delete(card);
        log.info("Card '{}' was deleted", maskedNumber);
    }

    @Override
    public List<CardDto> getCards(CardSearchParam params) {
        log.trace("Get cards request {}", params);
        Pageable page = PageRequest.of(params.getFrom() / params.getSize(), params.getSize());
        List<Card> cards = cardRepository.findAll(CardSpecifications.cardSearchSpec(params), page).getContent();
        cards.forEach(card -> card.setNumber(CardNumberMasker.mask(card.getNumber())));
        return cardMapper.toCardDto(cards);
    }

    @Override
    public CardFullDto getCard(String cardNumber) {
        String maskedNumber = CardNumberMasker.mask(cardNumber);
        log.trace("Request to get card {}", maskedNumber);
        Card card = cardRepository.findByNumberJoinOwner(cardNumber)
                .orElseThrow(() -> new NotFoundException(String.format("Card with number %s not found", maskedNumber)));
        return cardMapper.toCardFullDto(card, maskedNumber);
    }

    @Override
    public List<CardBlockingRequestFullDto> getCardBlockingRequests(CardBlockingRequestSearchParam params) {
        log.trace("Get card blocking requests {}", params);
        Pageable page = PageRequest.of(params.getFrom() / params.getSize(), params.getSize());
        List<CardBlockingRequest> requests = cardBlockingRequestRepository
                .findAll(CardBlockingRequestSpecifications.cardBlockingRequestSearchSpec(params), page).getContent();
        requests.forEach(request -> request.setCardNumber(CardNumberMasker.mask(request.getCardNumber())));
        return cardBlockingRequestMapper.toDto(requests);
    }
}
