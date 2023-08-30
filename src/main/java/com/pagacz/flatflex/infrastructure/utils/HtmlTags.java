package com.pagacz.flatflex.infrastructure.utils;

public enum HtmlTags {
    TD_STYLE("<td style=\"%s\">", "</td>"),
    TD_DEFAULT("<td>", "</td>"),
    TABLE_STYLE("<table style=\"%s\">", "</table>"),
    TR_DEFAULT("<tr>", "</tr>"),
    TR_STYLE("<tr style=\"%s\">", "</tr>"),
    THEAD_DEFAULT("<thead>", "</thead>");

    private final String openTag;
    private final String closeTag;

    HtmlTags(String openTag, String closeTag) {
        this.openTag = openTag;
        this.closeTag = closeTag;
    }

    public String getOpenTag() {
        return openTag;
    }

    public String getCloseTag() {
        return closeTag;
    }
}
