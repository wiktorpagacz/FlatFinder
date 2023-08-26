package com.pagacz.flatflex.infrastructure.persistence;

import com.pagacz.flatflex.application.service.*;
import com.pagacz.flatflex.domain.model.Offer;
import com.pagacz.flatflex.domain.repository.OfferRepository;
import com.pagacz.flatflex.domain.utils.OfferStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class OfferAggregatorServiceImpl implements OfferAggregatorService {

    private final Logger log = LoggerFactory.getLogger(OfferAggregatorServiceImpl.class);

    @Autowired
    private OfferRepository offerRepository;
    @Autowired
    private List<OfferService> offerServices;

    @Override
    public void aggregateNewOffers() {
        List<Offer> aggregatedOffers = new ArrayList<>();
        try {
            aggregatedOffers = aggregateOffersAsync().join();
        } catch (Exception e) {
            log.error("An asynchronous aggregating throw an exception: {}", e.getMessage());
        } finally {
            if (!aggregatedOffers.isEmpty()) {
                aggregatedOffers = prepareDistinctOffers(aggregatedOffers);
                offerRepository.saveAll(aggregatedOffers);
            }
        }

    }

    @Override
    public List<Offer> getNotWroteOffers() {
        return offerRepository.getNotWroteOffers(OfferStatus.OFFER_NOT_WROTE.getStatus());
    }

    public void updateErrorSendOffers() {
        offerRepository.updateErrorSendOffersToNotSend(OfferStatus.OFFER_NOT_SEND.getStatus(), OfferStatus.OFFER_SEND_ERROR.getStatus());
    }

    @Override
    public void updateErrorWroteOffers() {
        offerRepository.updateErrorWriteToNotWrote(OfferStatus.OFFER_WRITE_ERROR.getStatus(), OfferStatus.OFFER_NOT_WROTE.getStatus());
    }

    @Override
    public List<Offer> getLastWroteOffers(int limit) {
        return offerRepository.getLastWroteOffers(limit, OfferStatus.OFFER_WROTE_STATUS.getStatus());
    }

    @Override
    public List<Offer> getOffersToSend() {
        return offerRepository.getOffersBySendByEmailStatus(OfferStatus.OFFER_NOT_SEND.getStatus());
    }

    @Async
    public CompletableFuture<List<Offer>> aggregateOffersAsync() {
        List<CompletableFuture<List<Offer>>> asyncTasks = new ArrayList<>();

        for (OfferService offerService : offerServices) {
            asyncTasks.add(offerService.getNewOffersAsync());
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

    @Override
    public void markOfferSendAsError(List<Offer> offersToSend) {
        offersToSend.forEach(o -> o.setSendByEmail(OfferStatus.OFFER_SEND_ERROR.getStatus()));
        offerRepository.saveAll(offersToSend);
    }

    @Override
    public void markOfferSendAsSuccessful(List<Offer> offersToSend, LocalDateTime sendTime) {
        offersToSend.forEach(o -> o.setSendByEmail(OfferStatus.OFFER_SEND_STATUS.getStatus()));
        offersToSend.forEach(o -> o.setSendByEmailTime(sendTime));
        offerRepository.saveAll(offersToSend);
    }

    private List<Offer> prepareDistinctOffers(List<Offer> scrappedOffers) {
        List<Offer> distinctOffers = getDistinctOffersByLink(scrappedOffers);
        return comparePriceWithDb(distinctOffers);
    }

    private List<Offer> comparePriceWithDb(List<Offer> offers) {
        for (int i = 0; i < offers.size(); i++) {
            Offer offer = offers.get(i);
            Optional<Offer> oldOffer = offerRepository.findByLink(offer.getLink());
            if (oldOffer.isPresent()) {
                if (offerPriceHasChanged(oldOffer.get(), offer)) {
                    updateOfferPriceComment(offer, oldOffer.get());
                }
                offers.set(i, oldOffer.get());
            }
        }
        return offers;
    }

    private List<Offer> getDistinctOffersByLink(List<Offer> offers) {
        Map<String, Offer> offerMap = offers.stream()
                .collect(Collectors.toMap(Offer::getLink, Function.identity(),
                        (offer1, offer2) -> offer1));
        return new ArrayList<>(offerMap.values());
    }

    private boolean offerPriceHasChanged(Offer oldOffer, Offer offer) {
        return !Objects.equals(oldOffer.getPrice(), offer.getPrice());
    }

    private void updateOfferPriceComment(Offer newOffer, Offer oldOffer) {
        oldOffer.setComment("Nowa cena! Stara: " + oldOffer.getPrice() + " z dnia " + oldOffer.getInsertDate());
        oldOffer.setPrice(newOffer.getPrice());
    }

    @Override
    public void saveOffers(List<Offer> updatedOffers) {
        offerRepository.saveAll(updatedOffers);
    }

    @Override
    public void saveOffer(Offer offer) {
        offerRepository.save(offer);
    }
}
