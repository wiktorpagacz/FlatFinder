package com.pagacz.flatflex.infrastructure.mail;

import com.pagacz.flatflex.domain.model.Offer;
import com.pagacz.flatflex.infrastructure.utils.HtmlStyle;
import com.pagacz.flatflex.infrastructure.utils.HtmlTags;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class MailCreatorService {

    private static final String LINK_NAME = "LINK";

    private static final List<String> TABLE_HEADERS = new ArrayList<>(
            List.of("Nr",
                    "Serwis",
                    "Komentarz",
                    "Tytuł",
                    "Cena",
                    "Wielkość",
                    "Adres",
                    "Link")
    );

    public String createMailFromOffers(List<Offer> offerList) {
        StringBuilder mailMessage = new StringBuilder();
        if (Objects.nonNull(offerList) && !offerList.isEmpty()) {
            int iterator = 0;
            createTableHeader(mailMessage);
            for (Offer offer : offerList) {
                createOfferRow(mailMessage, offer, iterator);
                iterator++;
            }
            mailMessage.append(HtmlTags.TABLE_STYLE.getCloseTag());
        }
        return mailMessage.toString();
    }

    private void createTableColumn(StringBuilder builder, String columnName, HtmlStyle style) {
        builder.append(String.format(HtmlTags.TD_STYLE.getOpenTag(), style.getStyle())).append(columnName).append(HtmlTags.TD_STYLE.getCloseTag());
    }

    private void createTableHeader(StringBuilder tableBuilder) {
        tableBuilder.append(String.format(HtmlTags.TABLE_STYLE.getOpenTag(), HtmlStyle.TABLE.getStyle()));
        tableBuilder.append(HtmlTags.THEAD_DEFAULT.getOpenTag());
        tableBuilder.append(HtmlTags.TR_DEFAULT.getOpenTag());
        prepareHeadColumns(tableBuilder);
        tableBuilder.append(HtmlTags.TR_DEFAULT.getCloseTag());
        tableBuilder.append(HtmlTags.THEAD_DEFAULT.getCloseTag());
    }

    private void prepareHeadColumns(StringBuilder tableBuilder) {
        MailCreatorService.TABLE_HEADERS.forEach(colName -> createTableColumn(tableBuilder, colName, HtmlStyle.TH));
    }

    private void createOfferRow(StringBuilder builder, Offer offer, int rowNumber) {
        builder.append(String.format(HtmlTags.TR_STYLE.getOpenTag(), rowNumber % 2 == 0 ? HtmlStyle.TR_EVEN.getStyle() : HtmlStyle.TR_ODD.getStyle()));
        createTableColumn(builder, String.valueOf(rowNumber), HtmlStyle.TD);
        createTableColumn(builder, String.valueOf(offer.getSource()), HtmlStyle.TD);
        createTableColumn(builder, String.valueOf(offer.getComment()), HtmlStyle.TD);
        createTableColumn(builder, String.valueOf(offer.getTitle()), HtmlStyle.TD);
        createTableColumn(builder, String.valueOf(offer.getPrice()), HtmlStyle.TD);
        createTableColumn(builder, String.valueOf(offer.getSpace()), HtmlStyle.TD);
        createTableColumn(builder, String.valueOf(offer.getAddress()), HtmlStyle.TD);
        createTableColumn(builder, createAnchor(offer.getLink()), HtmlStyle.TD);
        builder.append(HtmlTags.TR_DEFAULT.getCloseTag());
    }

    private String createAnchor(String href) {
        return String.format("<a href=\"%s\">%s</a>", href, MailCreatorService.LINK_NAME);
    }
}
