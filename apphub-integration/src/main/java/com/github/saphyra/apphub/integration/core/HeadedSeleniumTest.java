package com.github.saphyra.apphub.integration.core;

import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.util.concurrent.Semaphore;

@Slf4j
public abstract class HeadedSeleniumTest extends SeleniumTest {
    private static final Semaphore SEMAPHORE = new Semaphore(1, true);

    @BeforeMethod(alwaysRun = true)
    public void before() throws InterruptedException {
        SEMAPHORE.acquire(1);
    }

    @AfterMethod(alwaysRun = true)
    public void after() {
        SEMAPHORE.release(1);
    }
}
