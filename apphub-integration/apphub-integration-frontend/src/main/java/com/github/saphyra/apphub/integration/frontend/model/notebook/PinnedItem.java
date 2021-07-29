package com.github.saphyra.apphub.integration.frontend.model.notebook;

import com.github.saphyra.apphub.integration.common.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.frontend.service.notebook.PinnedItemActions;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static org.assertj.core.api.Assertions.assertThat;

@AllArgsConstructor
@Slf4j
public class PinnedItem {
    private final WebElement webElement;

    public String getTitle() {
        return webElement.findElement(By.cssSelector(":scope span")).getText();
    }

    public void open() {
        webElement.click();
    }

    public void unpin(WebDriver driver) {
        String title = getTitle();
        int pinnedItemsAmount = PinnedItemActions.getPinnedItems(driver).size();

        webElement.findElement(By.cssSelector(":scope button")).click();

        AwaitilityWrapper.createDefault()
            .until(() -> PinnedItemActions.getPinnedItems(driver).size() == pinnedItemsAmount - 1)
            .assertTrue(title + " is not unpinned.");

        assertThat(PinnedItemActions.getPinnedItems(driver).stream().map(PinnedItem::getTitle)).doesNotContain(title);
    }
}
