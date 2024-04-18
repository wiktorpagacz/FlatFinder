package com.pagacz.flatflex.infrastructure.kafka;

import com.pagacz.flatflex.domain.model.Offer;
import generated.avro.FlatData;

import java.util.List;

public class OfferEventHelper {

    private OfferEventHelper() {
    }

    public static List<FlatData> prepareEventsData(List<Offer> offers) {
        return offers.stream().map(OfferEventHelper::prepareSingleEvent).toList();
    }

    private static FlatData prepareSingleEvent(Offer offer) {
        FlatData flatData = new FlatData();
        flatData.setPrice(offer.getPrice());
        flatData.setTitle(offer.getTitle());
        flatData.setOriginalPrice(offer.getOriginalPrice());
        flatData.setAddress(offer.getAddress());
        flatData.setLink(offer.getLink());
        flatData.setSource(offer.getSource());
        flatData.setComment(offer.getComment());
        flatData.setSpace(offer.getSpace());
        return flatData;
    }
}
