package com.github.saphyra.apphub.test.backend;

import org.testng.annotations.Test;

public class FailTest {
    @Test()
    public void t() {
        throw new RuntimeException();
    }
}
