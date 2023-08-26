package com.pagacz.flatflex.infrastructure.webdriver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.net.MalformedURLException;
import java.net.URL;

public class FirefoxBrowser implements DriverFactory {

    private final Logger log = LoggerFactory.getLogger(FirefoxBrowser.class);

    @Value("APP_ENVIRONMENT")
    private String env;

    @Override
    public WebDriver getDriver() {
        WebDriver driver = null;
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("-private");
        if ("local".equals(env)) {
            System.setProperty("webdriver.gecko.driver", "path/to/geckodriver.exe");
            driver = new FirefoxDriver(options);
        } else {
            options.setHeadless(true);
            System.setProperty("webdriver.gecko.driver", "/usr/bin/geckodriver");
            try {
                driver = new RemoteWebDriver(new URL("http://firefox:4444/wd/hub"), options);
            } catch (MalformedURLException e) {
                log.error("Błąd przy tworzeniu geckodrivera {}", e.getMessage());
            }
        }
        return driver;
    }
}
