package com.github.saphyra.apphub.integration.common;

import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import java.util.Objects;

@Slf4j
public class TestBase {
    public static int SERVER_PORT;

    @BeforeSuite(alwaysRun = true)
    public void setUpSuite() {
        SERVER_PORT = Integer.parseInt(Objects.requireNonNull(System.getProperty("serverPort"), "serverPort is null"));
        log.info("ServerPort: {}", SERVER_PORT);
    }

    @AfterSuite(alwaysRun = true)
    public void tearDownSuite() {

    }
}
