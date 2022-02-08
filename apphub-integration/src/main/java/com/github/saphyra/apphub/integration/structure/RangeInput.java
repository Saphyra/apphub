package com.github.saphyra.apphub.integration.structure;

import lombok.RequiredArgsConstructor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

@RequiredArgsConstructor
public class RangeInput {
    private final WebElement input;

    public void setValue(int value) {
        for (int i = 0; i < 100; i++) {
            int inputValue = Integer.parseInt(input.getAttribute("value"));

            if (inputValue == value) {
                return;
            }

            input.sendKeys(value > inputValue ? Keys.RIGHT : Keys.LEFT);
        }
    }
}
