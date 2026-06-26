package com.github.saphyra.apphub.integration.structure.view.task_manager;

import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.WebElement;

import java.util.List;

@RequiredArgsConstructor
public class Notification {
    private final WebElement webElement;

    public List<String> getClasses() {
        return WebElementUtils.getClasses(webElement);
    }
}
