package com.pagacz.flatflex.application.scheduler;

import com.pagacz.flatflex.application.service.OfferAggregatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Lazy(value = false)
@Component
public class ScheduledSearches {

    private static final Logger log = LoggerFactory.getLogger(ScheduledSearches.class);
    private final OfferAggregatorService offerAggregatorService;

    @Autowired
    public ScheduledSearches(OfferAggregatorService offerAggregatorService) {
        this.offerAggregatorService = offerAggregatorService;
    }

    @Scheduled(cron = "0 */5 9-23 ? * *")
    public void scrapOffersAndSaveInDb() {
        log.info("Scheduled task taskScrapOffers started.");
        offerAggregatorService.aggregateNewOffers();
        log.info("Scheduled task taskScrapOffers finished.");
    }
}
