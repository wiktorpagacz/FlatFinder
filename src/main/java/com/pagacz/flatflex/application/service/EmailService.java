package com.pagacz.flatflex.application.service;

import com.pagacz.flatflex.domain.model.Offer;

import java.util.List;

public interface EmailService {

    void sendEmailNew(String subject, String mail, String recipients, String sender);

    void sendEmailWithOffers(String email, String emailRecipients, String emailSender);

    void prepareAndSendMailWithOffers(List<Offer> offerList);
}
