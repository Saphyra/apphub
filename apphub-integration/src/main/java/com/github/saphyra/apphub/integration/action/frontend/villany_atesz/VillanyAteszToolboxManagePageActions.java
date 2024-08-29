package com.github.saphyra.apphub.integration.action.frontend.villany_atesz;

import com.github.saphyra.apphub.integration.structure.view.villany_atesz.ToolboxManagedStorageBox;
import com.github.saphyra.apphub.integration.structure.view.villany_atesz.ToolboxManagedToolType;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.List;
import java.util.stream.Collectors;

public class VillanyAteszToolboxManagePageActions {
    public static List<ToolboxManagedToolType> getToolTypes(WebDriver driver) {
        return driver.findElements(By.cssSelector("#villany-atesz-toolbox-manage-tool-types .villany-atesz-toolbox-manage-tab-content-item"))
            .stream()
            .map(ToolboxManagedToolType::new)
            .collect(Collectors.toList());
    }

    public static List<ToolboxManagedStorageBox> getStorageBoxes(WebDriver driver) {
        return driver.findElements(By.cssSelector("#villany-atesz-toolbox-manage-storage-boxes .villany-atesz-toolbox-manage-tab-content-item"))
            .stream()
            .map(ToolboxManagedStorageBox::new)
            .collect(Collectors.toList());
    }
}
