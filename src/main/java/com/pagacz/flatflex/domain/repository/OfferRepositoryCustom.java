package com.pagacz.flatflex.domain.repository;

import com.pagacz.flatflex.domain.model.Offer;

import java.time.LocalDateTime;
import java.util.List;

public interface OfferRepositoryCustom {

    List<Offer> getNotWroteOffers(char status);

    void updateErrorSendOffersToNotSend(char notSendStatus, char errorStatus);

    void updateErrorWriteToNotWrote(char notWroteStatus, char errorStatus);

    void updateWroteOffers(List<Long> offersListId, char status, LocalDateTime writeToDocsTime);

    List<Offer> getLastWroteOffers(int limit, char status);

    void updateWriteToDocsTime(List<Long> updatedOffersIds, LocalDateTime writeToDocsTime);

    List<Offer> getOffersBySendByEmailStatus(char offerStatus);

}
