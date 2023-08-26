package com.pagacz.flatflex.infrastructure.gsheet;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.pagacz.flatflex.application.service.GoogleSheetService;
import com.pagacz.flatflex.application.service.OfferAggregatorService;
import com.pagacz.flatflex.domain.model.Offer;
import com.pagacz.flatflex.domain.utils.OfferStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class GoogleSheetServiceImpl implements GoogleSheetService {

    private final Logger log = LoggerFactory.getLogger(GoogleSheetServiceImpl.class);
    private static final String APPLICATION_NAME = "flatflex";
    @Value("${SHEET_ID}")
    private String SPREADSHEET_ID;
    @Value("${SHEET_RANGE}")
    private String SHEET_RANGE;
    private Sheets sheetsService;
    @Autowired
    private OfferAggregatorService offerAggregatorService;

    public GoogleSheetServiceImpl() throws IOException, GeneralSecurityException {
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

        InputStream credentialsStream = GoogleSheetServiceImpl.class.getResourceAsStream("/credentials2.json");
        if (Objects.nonNull(credentialsStream)) {
            log.info("[GOOGLESHEET] credentialsStream loaded.");
        }
        GoogleCredential credential = GoogleCredential.
                fromStream(credentialsStream)
                .createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS));

        sheetsService = new Sheets.Builder(httpTransport, jsonFactory, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public void addAllOffers(List<Offer> offers) {
        for (Offer offer : offers) {
            addOffer(offer);
        }
    }

    @Override
    public void updateExistingOffers(List<Offer> offers) {
        List<Offer> updatedOffers;
        Map<String, Offer> linkOfferMap = prepareLinkOfferMap(offers);
        ValueRange existingOffersResponse = getExistingOffers();
        if (Objects.nonNull(existingOffersResponse) && Objects.nonNull(existingOffersResponse.getValues())
                && !existingOffersResponse.values().isEmpty()) {
            updatedOffers = updateOffersInSheet(existingOffersResponse.getValues(), linkOfferMap);
            offers.removeAll(updatedOffers);
        }
    }

    private List<Offer> updateOffersInSheet(List<List<Object>> sheetRows, Map<String, Offer> linkOfferMap) {
        List<Offer> updatedOffers = new ArrayList<>();
        for (List<Object> offerSheetRow : sheetRows) {
            Offer offerFromDb = getOfferByLink(linkOfferMap, offerSheetRow);
            if (Objects.nonNull(offerFromDb) && priceChanged(offerFromDb, offerSheetRow)) {
                offerAggregatorService.saveOffer(offerFromDb);
                String updateRange = createRangeForPriceChange(sheetRows, offerSheetRow);
                List<List<Object>> updateValues = createValuesForPriceChange(offerSheetRow, offerFromDb);
                try {
                    updateOfferInSheet(updateRange, updateValues);
                    updatedOffers.add(offerFromDb);
                } catch (IOException e) {
                    log.error("Error occurred at offer updating in gsheet", e);
                }
            }
        }
        return updatedOffers;
    }

    private void updateOfferInSheet(String updateRange, List<List<Object>> updateValues) throws IOException {
        ValueRange body = new ValueRange().setValues(updateValues);
        sheetsService.spreadsheets()
                .values()
                .update(SPREADSHEET_ID, updateRange, body)
                .setValueInputOption("RAW")
                .execute();
    }

    private List<List<Object>> createValuesForPriceChange(List<Object> row, Offer offer) {
        return List.of(Arrays.asList("Nowa cena! Stara: " + row.get(5), offer.getPrice(),
                offer.getSpace(), offer.getAddress(), String.valueOf(offer.getInsertDate()), String.valueOf(offer.getLastModifiedTime())));
    }

    private String createRangeForPriceChange(List<List<Object>> values, List<Object> row) {
        return "Arkusz1!E" + (values.indexOf(row) + 2) + ":K" + (values.indexOf(row) + 2);
    }

    private boolean priceChanged(Offer offer, List<Object> row) {
        return !offer.getPrice().equals(Integer.valueOf(String.valueOf(row.get(5))));
    }

    private Offer getOfferByLink(Map<String, Offer> linkOfferMap, List<Object> offerSheetRow) {
        String link = (String) offerSheetRow.get(3);
        Offer offer = linkOfferMap.get(link);
        if (Objects.nonNull(offer)) {
            linkOfferMap.remove(link);
        }
        return offer;
    }

    private ValueRange getExistingOffers() {
        ValueRange response = null;
        try {
            response = sheetsService.spreadsheets().values().get(SPREADSHEET_ID, SHEET_RANGE).execute();
        } catch (IOException e) {
            log.error("Error occurred at getting offers from spreadsheet stage", e);
        }
        return response;
    }

    private Map<String, Offer> prepareLinkOfferMap(List<Offer> offers) {
        return offers.stream().collect(Collectors.toMap(Offer::getLink, Function.identity()));
    }

    public void addOffer(Offer offer) {
        try {
            prepareAndWriteOfferToDocs(offer);
            offer.setWriteToDocsTime(LocalDateTime.now());
            offer.setWriteToDocs(OfferStatus.OFFER_SUCCESS_UPDATE_STATUS.getStatus());
        } catch (IOException e) {
            log.error("Error occurred at add offer to gsheet stage", e);
            offer.setWriteToDocs(OfferStatus.OFFER_WRITE_ERROR.getStatus());
        }
    }

    private void prepareAndWriteOfferToDocs(Offer offer) throws IOException {
        List<List<Object>> values = List.of(
                Arrays.asList(
                        offer.getId(),
                        offer.getTitle(),
                        offer.getSource(),
                        offer.getLink(),
                        offer.getComment(),
                        offer.getPrice(),
                        offer.getSpace(),
                        offer.getAddress(),
                        DateTime.parseRfc3339(offer.getInsertDate().toString()).toString(),
                        DateTime.parseRfc3339(offer.getLastModifiedTime().toString()).toString()
                )
        );
        ValueRange body = new ValueRange().setValues(values);
        sheetsService.spreadsheets().values()
                .append(SPREADSHEET_ID, SHEET_RANGE, body)
                .setValueInputOption("RAW")
                .execute();
    }

}
