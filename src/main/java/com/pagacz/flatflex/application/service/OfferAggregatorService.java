package com.pagacz.flatflex.application.service;

public interface OfferAggregatorService {

    void aggregateNewOffers();
    void sendOfferViaKafka();
    void clearSentOffers();
}
