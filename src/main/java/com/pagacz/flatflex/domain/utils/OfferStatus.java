package com.pagacz.flatflex.domain.utils;

public enum OfferStatus {

    OFFER_SEND('Y'),
    OFFER_NOT_SEND('N');
    private final char status;

    OfferStatus(char status) {
        this.status = status;
    }

    public char getStatus() {
        return this.status;
    }
}
