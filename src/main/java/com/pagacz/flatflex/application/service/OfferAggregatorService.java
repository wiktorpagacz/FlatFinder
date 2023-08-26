package com.pagacz.flatflex.application.service;

import com.pagacz.flatflex.domain.model.Offer;

import java.time.LocalDateTime;
import java.util.List;

public interface OfferAggregatorService {

    void aggregateNewOffers();

    List<Offer> getNotWroteOffers();

    void updateErrorSendOffers();

    void updateErrorWroteOffers();

    List<Offer> getLastWroteOffers(int number);

    void saveOffers(List<Offer> updatedOffers);

    void saveOffer(Offer offer);

    List<Offer> getOffersToSend();

    void markOfferSendAsError(List<Offer> offersToSend);

    void markOfferSendAsSuccessful(List<Offer> offersToSend, LocalDateTime sendTime);
}
