package com.pagacz.flatflex.infrastructure.utils;

public enum OfferStatus {
    OFFER_SEND_STATUS('Y'),
    OFFER_WROTE_STATUS('Y'),
    OFFER_NOT_WROTE('N'),
    OFFER_NOT_SEND('N'),
    OFFER_SEND_ERROR('E'),
    OFFER_WRITE_ERROR('E'),
    OFFER_SUCCESS_UPDATE_STATUS('Y');

    private final char status;

    OfferStatus(char status) {
        this.status = status;
    }

    public char getStatus() {
        return this.status;
    }
}
