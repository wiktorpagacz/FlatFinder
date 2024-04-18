package com.pagacz.flatflex.infrastructure.persistence;

import com.pagacz.flatflex.application.service.OfferScrapperService;
import com.pagacz.flatflex.domain.model.Offer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class OfferAggregatorAsync {

    private final List<OfferScrapperService> offerScrapperServices;

    @Autowired
    public OfferAggregatorAsync(List<OfferScrapperService> offerScrapperServices) {
        this.offerScrapperServices = offerScrapperServices;
    }

    @Async
    public CompletableFuture<List<Offer>> aggregateOffersAsync() {
        List<CompletableFuture<List<Offer>>> asyncTasks = new ArrayList<>();
        for (OfferScrapperService offerScrapperService : offerScrapperServices) {
            asyncTasks.add(offerScrapperService.getNewOffersAsync());
        }
        CompletableFuture<Void> allOf = CompletableFuture.allOf(asyncTasks.toArray(new CompletableFuture[0]));
        return allOf.thenApply(v -> {
            List<Offer> newOffers = new ArrayList<>();
            for (CompletableFuture<List<Offer>> asyncTask : asyncTasks) {
                newOffers.addAll(asyncTask.join());
            }
            return newOffers;
        });
    }
}
