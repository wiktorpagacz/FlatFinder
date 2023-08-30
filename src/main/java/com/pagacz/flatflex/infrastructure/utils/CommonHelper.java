package com.pagacz.flatflex.infrastructure.utils;

public final class CommonHelper {

    private CommonHelper() {
    }

    private static final String PLN_CURRENCY = "zł";
    public static final int GOOGLE_API_QUERY_LIMIT = 50;

    public static boolean priceIsNotGiven(String priceValue) {
        return priceValue.contains("Zapytaj");
    }

    public static String removeCurrency(String price) {
        return price.replace(PLN_CURRENCY, "");
    }
}
