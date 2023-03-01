package com.dart.ssh;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TestSelenium {

    @Test
    public void test() {
        // set the location of the chrome driver executable
        System.setProperty("webdriver.chrome.driver", "path/to/chromedriver");

        // create a new instance of the ChromeDriver
        WebDriver driver = new ChromeDriver();

        // navigate to the website you want to test
        driver.get("https://www.example.com");

        // perform actions on the website
        // for example, click a link or fill out a form

        // close the browser
        driver.quit();
    }
}
