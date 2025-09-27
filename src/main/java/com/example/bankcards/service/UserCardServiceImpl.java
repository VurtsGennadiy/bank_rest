package com.example.bankcards.service;

import com.example.bankcards.dto.CardBalanceDto;
import com.example.bankcards.dto.CardBlockingRequestDto;
import com.example.bankcards.dto.CardShortDto;
import com.example.bankcards.dto.MoneyTransferRequest;
import com.example.bankcards.dto.filters.CardSearchParam;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardBlockingRequest;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.MoneyTransfer;
import com.example.bankcards.exception.MoneyTransferException;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.repository.CardBlockingRequestRepository;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.MoneyTransferRepository;
import com.example.bankcards.util.CardNumberMasker;
import com.example.bankcards.util.mapper.CardBlockingRequestMapper;
import com.example.bankcards.util.mapper.CardMapper;
import com.example.bankcards.util.specification.CardSpecifications;
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
    private final MoneyTransferRepository moneyTransferRepository;
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

    @Override
    public CardBalanceDto getBalance(Long userId, String partCardNumber) {
        log.trace("Request to get balance card '{}' from user with id '{}'", partCardNumber, userId);
        Card card = cardRepository.findCardByOwner_IdAndNumberContaining(userId, partCardNumber)
                .orElseThrow(() -> new NotFoundException(String.format("Card with number %s not found", partCardNumber)));
        String maskedCardNumber = CardNumberMasker.mask(card.getNumber());
        return new CardBalanceDto(maskedCardNumber, card.getBalance());
    }

    @Override
    @Transactional
    public CardBalanceDto cardToCardTransfer(MoneyTransferRequest request) {
        log.trace("Request to transfer money: {}", request);
        Card fromCard = cardRepository.findCardByOwner_IdAndNumberContaining(request.getUserId(), request.getFromCardNumber())
                .orElseThrow(() -> new NotFoundException(
                        String.format("Card with number %s not found", request.getFromCardNumber())));

        Card toCard = cardRepository.findCardByOwner_IdAndNumberContaining(request.getUserId(), request.getToCardNumber())
                .orElseThrow(() -> new NotFoundException(
                        String.format("Card with number %s not found", request.getToCardNumber())));

        String fromCardMaskedNumber = CardNumberMasker.mask(fromCard.getNumber());
        String toCardMaskedNumber = CardNumberMasker.mask(toCard.getNumber());

        if (fromCard.getBalance().compareTo(request.getAmount()) < 0) {
            throw new MoneyTransferException("Insufficient funds on the card to perform the operation");
        }
        if (CardStatus.ACTIVE != fromCard.getStatus()) {
            throw new MoneyTransferException(String.format("The card %s status is not ACTIVE", fromCardMaskedNumber));
        }
        if (CardStatus.ACTIVE != toCard.getStatus()) {
            throw new MoneyTransferException(String.format("The card %s status is not ACTIVE", toCardMaskedNumber));
        }

        MoneyTransfer transfer = MoneyTransfer.builder()
                .userId(request.getUserId())
                .toCardNumber(toCard.getNumber())
                .fromCardNumber(fromCard.getNumber())
                .amount(request.getAmount())
                .build();

        try {
            fromCard.setBalance(fromCard.getBalance().subtract(transfer.getAmount()));
            toCard.setBalance(toCard.getBalance().add(transfer.getAmount()));
            moneyTransferRepository.save(transfer);
        } catch (Exception e) {
            log.error("Error executing money transfer", e);
            throw new MoneyTransferException("Error executing money transfer");
        }
        log.info("Success executing money transfer from: '{}' to: '{}' amount: '{}'",
                fromCardMaskedNumber, toCardMaskedNumber, transfer.getAmount());
        return new CardBalanceDto(CardNumberMasker.mask(fromCard.getNumber()), fromCard.getBalance());
    }
}
