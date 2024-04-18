package com.pagacz.flatflex.infrastructure.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pagacz.flatflex.application.service.*;
import com.pagacz.flatflex.domain.model.Offer;
import com.pagacz.flatflex.domain.repository.OfferRepository;
import com.pagacz.flatflex.domain.utils.OfferStatus;
import com.pagacz.flatflex.infrastructure.cache.RedisCacheService;
import com.pagacz.flatflex.infrastructure.kafka.KafkaProducer;
import com.pagacz.flatflex.infrastructure.kafka.OfferEventHelper;
import com.pagacz.flatflex.infrastructure.kafka.utils.LocalDateTimeTypeAdapter;
import generated.avro.FlatData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    private final RedisCacheService redisCacheService;
    private final OfferRepository offerRepository;
    private final KafkaProducer producer;
    private final OfferAggregatorAsync offerAggregatorAsync;
    @Value(value = "${offer.delete.threshold}")
    private int offerDeleteThreshold;

    @Autowired
    public OfferAggregatorServiceImpl(RedisCacheService redisCacheService, OfferRepository offerRepository,
                                      List<OfferScrapperService> offerScrapperServices, KafkaProducer kafkaProducer,
                                      OfferAggregatorAsync offerAggregatorAsync) {
        this.redisCacheService = redisCacheService;
        this.offerRepository = offerRepository;
        this.producer = kafkaProducer;
        this.offerAggregatorAsync = offerAggregatorAsync;
    }

    @Override
    public void aggregateNewOffers() {
        List<Offer> aggregatedOffers = new ArrayList<>();
        try {
            aggregatedOffers = offerAggregatorAsync.aggregateOffersAsync().join();
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
    public void sendOfferViaKafka() {
        List<Offer> offers = offerRepository.getOffersByStatus(OfferStatus.OFFER_NOT_SEND.getStatus());
        offers.forEach(o -> o.setKafkaSend(OfferStatus.OFFER_SEND.getStatus()));
        List<FlatData> offersToSend =  OfferEventHelper.prepareEventsData(offers);
        offersToSend.forEach(producer::sendMessage);
        offerRepository.saveAll(offers);
    }

    @Override
    public void clearSentOffers() {
        List<Offer> offers = offerRepository.getOffersByStatusAndDate(OfferStatus.OFFER_SEND.getStatus(), LocalDateTime.now().minusDays(offerDeleteThreshold));
        offerRepository.deleteAll(offers);
    }

    private List<Offer> prepareDistinctOffers(List<Offer> scrappedOffers) {
        List<Offer> distinctOffers = getDistinctOffersByLink(scrappedOffers);
        return comparePriceWithDb(distinctOffers);
    }

    private List<Offer> comparePriceWithDb(List<Offer> offers) {
        List<Offer> comparedOffers = new ArrayList<>();
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .create();
        for (Offer offer : offers) {
            String linkKey = "offer:" + offer.getLink();
            if (!redisCacheService.containsKey(linkKey)) {
                redisCacheService.addToCache(linkKey, gson.toJson(offer));
                comparedOffers.add(offer);
            } else {
                String jsonOffer = redisCacheService.get(linkKey);
                Offer storedOffer = gson.fromJson(jsonOffer, Offer.class);
                if (offerPriceHasChanged(storedOffer, offer)) {
                    updateOfferPriceComment(offer, storedOffer);
                    redisCacheService.addToCache(linkKey, gson.toJson(offer));
                    comparedOffers.add(offer);
                }
            }
        }
        return comparedOffers;
    }

    private List<Offer> getDistinctOffersByLink(List<Offer> offers) {
        Map<String, Offer> offerMap = offers.stream()
                .collect(Collectors.toMap(Offer::getLink, Function.identity(), (offer1, offer2) -> offer1));
        return new ArrayList<>(offerMap.values());
    }

    private boolean offerPriceHasChanged(Offer oldOffer, Offer offer) {
        return !Objects.equals(oldOffer.getPrice(), offer.getPrice());
    }

    private void updateOfferPriceComment(Offer newOffer, Offer oldOffer) {
        newOffer.setComment(newOffer.getComment() + "|" + LocalDateTime.now() + "| Nowa cena! Stara: " + oldOffer.getPrice());
        newOffer.setOriginalPrice(oldOffer.getOriginalPrice());
        if (oldOffer.getId() != null) {
            newOffer.setId(oldOffer.getId());
        } else {
            Optional<Offer> offerOpt = offerRepository.findByLink(newOffer.getLink());
            offerOpt.ifPresent(offer -> newOffer.setId(offer.getId()));
        }
    }
}
