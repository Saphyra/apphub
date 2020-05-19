package com.github.saphyra.apphub.integration.frontend.model.modules;

import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class Category {
    private static final By MODULES = By.cssSelector(".module");
    private final WebElement element;

    public List<Module> getModules() {
        return element.findElements(MODULES)
            .stream()
            .map(Module::new)
            .collect(Collectors.toList());
    }
}
