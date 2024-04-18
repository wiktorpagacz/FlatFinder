package com.pagacz.flatflex.infrastructure.first;

import com.pagacz.flatflex.application.service.OfferScrapperService;
import com.pagacz.flatflex.domain.utils.CommonHelper;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FirstScrapperServiceImpl extends OfferScrapperService {

    @Value("${A_API}")
    private String A_API;

    @Override
    protected String getCookiesSelector() {
        return FirstSelectors.COOKIES_ACCEPT_BUTTON.getValue();
    }

    @Override
    protected String getApiUrl() {
        return A_API;
    }

    @Override
    protected String getPageSelector() {
        return FirstSelectors.PAGE.getValue();
    }

    @Override
    protected String getServiceName() {
        return FirstSelectors.NAME.getValue();
    }

    @Override
    protected String getOfferCssSelector() {
        return FirstSelectors.OFFER_ELEMENT.getValue();
    }

    protected String selectAndFormatLink(Element offer) {
        String link = offer.select(FirstSelectors.OFFER_LINK.getValue()).attr("href");
        if (isOfferFromOtherPage(link)) {
            link = prepareLinkFromOtherSite(link);
        } else {
            link = FirstSelectors.BASE_URL.getValue() + link;
        }
        return link;
    }

    protected String selectAndFormatAddress(Element offer) {
        String address = offer.select(FirstSelectors.OFFER_ADDRESS.getValue()).text();
        address = address.substring(0, address.indexOf(" - "));
        return address;
    }

    protected Integer selectAndFormatPrice(Element offer) {
        String price = offer.select(FirstSelectors.OFFER_PRICE.getValue()).text().replace(" ", "");
        if (CommonHelper.priceIsNotGiven(price)) {
            return 0;
        } else {
            price = price.substring(0, 6);
            price = CommonHelper.removeCurrency(price);
        }
        return Integer.valueOf(price);
    }

    protected String selectTitleText(Element offer) {
        return offer.select(FirstSelectors.OFFER_TITLE.getValue()).text();
    }

    protected Double selectSpace(Element offer) {
        String space = offer.select(FirstSelectors.OFFER_SPACE.getValue()).text().substring(0, 2);
        return Double.valueOf(space);
    }

    private String prepareLinkFromOtherSite(String link) {
        link = link.replace("www.", "");
        link = link.replace(".html", "");
        return link;
    }

    private boolean isOfferFromOtherPage(String link) {
        return link.contains(FirstSelectors.OTHER_SITE.getValue());
    }
}
