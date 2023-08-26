package com.pagacz.flatflex.infrastructure.webdriver;

public class DriverFactoryProvider {

    private DriverFactoryProvider() {
        throw new IllegalStateException("Utility class");
    }

    public static DriverFactory getDriverFactory(String browserType) {
        if (browserType.equals("chrome")) {
            return new ChromeBrowser();
        } else {
            return new FirefoxBrowser();
        }
    }
}
