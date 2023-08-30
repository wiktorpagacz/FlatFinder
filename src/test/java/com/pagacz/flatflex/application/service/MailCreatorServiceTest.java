package com.pagacz.flatflex.application.service;

import com.pagacz.flatflex.domain.model.Offer;
import com.pagacz.flatflex.infrastructure.mail.MailCreatorService;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

class MailCreatorServiceTest {

    @Test
    void createMailFromOffersTest() throws IOException {
        List<Offer> dataItems = Arrays.asList(
                new Offer("https://test-link.com/offeraddress", "Title test", "Test page title", 359000, 330000, 59.4, "test address"),
                new Offer("https://test-link.com/offeraddress2", "Title test 2", "Other Test page title", 120000, 130000, 32.1, "other test address")
        );

        MailCreatorService mailCreatorService = new MailCreatorService();
        String generatedMail = mailCreatorService.createMailFromOffers(dataItems);

        File file = new File("src/test/java/com/pagacz/flatflex/application/resources/generated-mail-content.txt");
        String preparedMail = FileUtils.readFileToString(file, "UTF-8");

        Assertions.assertEquals(preparedMail, generatedMail);
    }
}
