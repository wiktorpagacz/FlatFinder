package com.pagacz.flatflex.infrastructure.webdriver;

import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ChromeBrowser implements DriverFactory {

    private final Logger log = LoggerFactory.getLogger(ChromeBrowser.class);

    @Override
    public WebDriver getDriver(String env) {
        System.setProperty("webdriver.chrome.driver", "chromedriver/chromedriver");
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--remote-allow-origins=*", "ignore-certificate-errors");
        chromeOptions.setPageLoadStrategy(PageLoadStrategy.NONE);
        return new ChromeDriver(chromeOptions);
    }
}
