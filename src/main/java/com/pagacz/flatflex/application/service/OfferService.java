package com.pagacz.flatflex.application.service;

import com.pagacz.flatflex.domain.model.Offer;
import com.pagacz.flatflex.infrastructure.webdriver.DriverFactory;
import com.pagacz.flatflex.infrastructure.webdriver.DriverFactoryProvider;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public abstract class OfferService {

    private final Logger log = LoggerFactory.getLogger(OfferService.class);

    @Value("${scrapper.browser}")
    String browserName;

    protected abstract String getServiceName();

    protected abstract String getOfferCssSelector();

    protected abstract String selectAndFormatLink(Element offer);

    protected abstract String selectAndFormatAddress(Element offer);

    protected abstract Integer selectAndFormatPrice(Element offer);

    protected abstract Double selectSpace(Element offer);

    protected abstract String selectTitleText(Element offer);

    protected abstract String getCookiesSelector();

    protected abstract String getBaseUrl();

    public List<Offer> getNewOffers() {
        log.info("Start scrapping from %s.");
        String pageHtml = getOffersCode();
        log.info("Offers scrapped.");
        return parseOffer(pageHtml);
    }

    @Async
    public CompletableFuture<List<Offer>> getNewOffersAsync() {
        return CompletableFuture.supplyAsync(() -> {
            log.info("Start scrapping from {}.", getServiceName());
            String pageHtml = getOffersCode();
            log.info("Offers scrapped.");
            return parseOffer(pageHtml);
        });
    }

    private String getOffersCode() {
        String baseUrl = getBaseUrl();
        DriverFactory factory = DriverFactoryProvider.getDriverFactory(browserName);
        WebDriver driver = factory.getDriver();
        driver.get(baseUrl);
        try {
            acceptCookies(driver);
            waitForOffersToAppear(driver);
        } catch (TimeoutException e) {
            log.error("Timeout exception occurred at cookies acceptance and wait for offers to appear stage", e);
            return "";
        } catch (Exception e) {
            log.error("Unexpected exception - quit driver", e);
        }
        return getPageHtmlAndQuitDriver(driver);
    }

    protected String getPageHtmlAndQuitDriver(WebDriver driver) {
        log.info("Getting html source code.");
        String source = "";
        try {
            source = driver.getPageSource();
        } catch (Exception e) {
            log.error("Error occurred at getting page source stage", e);
        } finally {
            log.debug("Quiting driver.");
            driver.quit();
            log.debug("Driver quit.");
        }
        return source;
    }

    protected void acceptCookies(WebDriver driver) {
        String cookiesSelector = getCookiesSelector();
        log.debug("Waiting for cookies to accept.");
        WebElement acceptCookiesButton = (new WebDriverWait(driver, Duration.of(35, ChronoUnit.SECONDS)))
                .until(ExpectedConditions.elementToBeClickable(By.cssSelector(cookiesSelector)));
        acceptCookiesButton.click();
        log.debug("Cookies accepted.");
    }

    protected void waitForOffersToAppear(WebDriver driver) {
        log.debug("Waiting for offers to appear.");
        String offerSelector = getPageSelector();
        WebDriverWait wait = new WebDriverWait(driver, Duration.of(35, ChronoUnit.SECONDS));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(offerSelector)));
        log.debug("Offers are presented.");
    }

    protected abstract String getPageSelector();

    protected List<Offer> parseOffer(String pageSourceCodeHtml) {
        Elements offerElements = parseHtmlAndSelectOffers(pageSourceCodeHtml);
        return prepareOffersFromElements(offerElements);
    }

    protected List<Offer> prepareOffersFromElements(Elements offerElements) {
        List<Offer> offerList = new ArrayList<>();
        for (Element offerElement : offerElements) {
            Offer offer = prepareOfferFromElement(offerElement);
            offerList.add(offer);
        }
        return offerList;
    }

    private Offer prepareOfferFromElement(Element offer) {
        String title = selectTitleText(offer);
        Double space = selectSpace(offer);
        Integer price = selectAndFormatPrice(offer);
        String address = selectAndFormatAddress(offer);
        String link = selectAndFormatLink(offer);
        String serviceName = getServiceName();
        return new Offer(link, title, serviceName, price, price, space, address);
    }

    protected Elements parseHtmlAndSelectOffers(String html) {
        String offerSelector = getOfferCssSelector();
        Document document = Jsoup.parse(html);
        return document.select(offerSelector);
    }
}
