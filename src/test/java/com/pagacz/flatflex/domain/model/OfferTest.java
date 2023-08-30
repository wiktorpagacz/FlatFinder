package com.pagacz.flatflex.domain.model;

import com.pagacz.flatflex.infrastructure.utils.OfferStatus;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class OfferTest {

    @Test
    void builderDefaultValuesTest() {
        Offer offer = Offer.builder().build();
        Assertions.assertEquals(OfferStatus.OFFER_NOT_WROTE.getStatus(), offer.getWriteToDocs());
        Assertions.assertEquals(OfferStatus.OFFER_NOT_SEND.getStatus(), offer.getSendByEmail());
        Assertions.assertEquals("", offer.getComment());
    }

    @Test
    void builderDefaultValuesNoArgConstructorTest() {
        Offer offer = new Offer();
        Assertions.assertEquals(OfferStatus.OFFER_NOT_WROTE.getStatus(), offer.getWriteToDocs());
        Assertions.assertEquals(OfferStatus.OFFER_NOT_SEND.getStatus(), offer.getSendByEmail());
        Assertions.assertEquals("", offer.getComment());
    }
}
