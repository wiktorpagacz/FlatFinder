package com.pagacz.flatflex.application.scheduler;

import com.pagacz.flatflex.domain.model.Offer;
import com.pagacz.flatflex.infrastructure.gsheet.GoogleSheetService;
import com.pagacz.flatflex.infrastructure.mail.EmailServiceImpl;
import com.pagacz.flatflex.infrastructure.persistence.OfferAggregatorService;
import com.pagacz.flatflex.infrastructure.utils.CommonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Lazy(value = false)
@Component
public class ScheduledSearches {

    private static final Logger log = LoggerFactory.getLogger(ScheduledSearches.class);
    private final EmailServiceImpl emailService;
    private final OfferAggregatorService offerAggregatorService;
    private final GoogleSheetService googleSheetService;

    @Autowired
    public ScheduledSearches(EmailServiceImpl emailService, OfferAggregatorService offerAggregatorService,
                             GoogleSheetService googleSheetService) {
        this.emailService = emailService;
        this.offerAggregatorService = offerAggregatorService;
        this.googleSheetService = googleSheetService;
    }

    @Scheduled(cron = "0 */5 9-23 ? * *")
    public void scrapOffersAndSaveInDb() {
        log.info("Scheduled task taskScrapOffers started.");
        offerAggregatorService.aggregateNewOffers();
        log.info("Scheduled task taskScrapOffers finished.");
    }

    @Scheduled(cron = "0 */10 6-23 ? * *")
    public void sendOffersByEmail() {
        log.info("Scheduled task scrapOffersAndSendEmail started.");
        sendEmailWithOffers();
        log.info("Scheduled task scrapOffersAndSendEmail finished.");
    }

    @Scheduled(cron = "0 */5 6-23 ? * *")
    public void taskWriteOffersToGoogleSheets() {
        log.info("Scheduled task writeOffersToGoogleSheets started.");
        addOffersToDocs();
        log.info("Scheduled task writeOffersToGoogleSheets finished.");
    }

    @Scheduled(cron = "0 */30 6-23 ? * *")
    public void setOffersWithErrorToWriteAndSend() {
        log.info("Scheduled task setOffersWithErrorToWriteAndSend started.");
        setOffersToWriteAndSend();
        log.info("Scheduled task setOffersWithErrorToWriteAndSend finished.");
    }


    private void addOffersToDocs() {
        List<Offer> offers = prepareOffersToWrite();
        googleSheetService.updateExistingOffers(offers);
        googleSheetService.addAllOffers(offers);
        offerAggregatorService.saveOffers(offers);
    }

    private void setOffersToWriteAndSend() {
        setOffersWithErrorEmailToNotSend();
        setOffersWithErrorWriteToNotWrote();
    }

    private void setOffersWithErrorEmailToNotSend() {
        offerAggregatorService.updateErrorSendOffers();
    }

    private void setOffersWithErrorWriteToNotWrote() {
        offerAggregatorService.updateErrorWroteOffers();
    }

    private List<Offer> prepareOffersToWrite() {
        List<Offer> notWroteOffers = offerAggregatorService.getNotWroteOffers();
        if (notWroteOffers.size() < CommonHelper.GOOGLE_API_QUERY_LIMIT) {
            notWroteOffers.addAll(offerAggregatorService.
                    getLastWroteOffers(CommonHelper.GOOGLE_API_QUERY_LIMIT - notWroteOffers.size()));
        }
        return notWroteOffers;
    }

    private void sendEmailWithOffers() {
        List<Offer> offersToSend = offerAggregatorService.getOffersToSend();
        LocalDateTime sendTime = LocalDateTime.now();
        try {
            emailService.prepareAndSendMailWithOffers(offersToSend);
            offerAggregatorService.markOfferSendAsSuccessful(offersToSend, sendTime);
        } catch (Exception e) {
            log.error("Exception occurred at prepare and send mail stage: ", e);
            offerAggregatorService.markOfferSendAsError(offersToSend);
        }
    }

}
