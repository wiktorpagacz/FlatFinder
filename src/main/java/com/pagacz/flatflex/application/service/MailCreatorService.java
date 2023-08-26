package com.pagacz.flatflex.application.service;

import com.pagacz.flatflex.domain.model.Offer;

import java.util.List;

public interface MailCreatorService {

    String createMailFromOffers(List<Offer> offerList);
}
