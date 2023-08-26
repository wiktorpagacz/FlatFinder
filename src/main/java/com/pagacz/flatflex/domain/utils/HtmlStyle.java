package com.pagacz.flatflex.domain.utils;

public enum HtmlStyle {
    TABLE("border-collapse: collapse; font-family: Tahoma, Geneva, sans-serif;"),
    TH("background-color: #54585d; color: #ffffff; font-weight: bold; font-size: 13px; border: 1px solid #54585d; padding: 15px;"),
    TR_ODD("background-color: #ffffff;"),
    TR_EVEN("background-color: #f9fafb;"),
    TD("color: #636363; border: 1px solid #dddfe1; padding: 15px;");

    public final String style;

    HtmlStyle(String style) {
        this.style = style;
    }

    public String getStyle() {
        return style;
    }
}
