package com.pagacz.flatflex.application.service;

import com.pagacz.flatflex.domain.model.Offer;

import java.util.List;

public interface GoogleSheetService {
    void addAllOffers(List<Offer> offers);

    void updateExistingOffers(List<Offer> offers);
}
