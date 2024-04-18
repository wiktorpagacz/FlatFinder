package com.pagacz.flatflex.application.scheduler;

import com.pagacz.flatflex.application.service.OfferAggregatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
public class OfferSender {
    private static final Logger log = LoggerFactory.getLogger(OfferSender.class);
    private final OfferAggregatorService offerAggregatorService;

    @Autowired
    public OfferSender(OfferAggregatorService offerAggregatorService) {
        this.offerAggregatorService = offerAggregatorService;
    }

    @Scheduled(cron = "0 */10 * ? * *")
    public void sendOffersByKafka() {
        log.info("Scheduled task sendOffersByKafka started.");
        offerAggregatorService.sendOfferViaKafka();
        log.info("Scheduled task sendOffersByKafka finished.");
    }

    public void clearSentOffers() {
        log.info("Scheduled task clearSentOffers started.");
        offerAggregatorService.clearSentOffers();
        log.info("Scheduled task finished started.");
    }
}
