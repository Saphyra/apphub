package com.github.saphyra.apphub.integration.action.frontend.task_manager;

import com.github.saphyra.apphub.integration.structure.view.task_manager.Notification;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.List;

public class TaskManagementOrganizationPageActions {
    public static int getUnreadNotificationCount(WebDriver driver) {
        return Integer.parseInt(driver.findElement(By.id("task-manager-organization-unread-notification-count")).getText());
    }

    public static List<Notification> getNotifications(WebDriver driver) {
        return driver.findElements(By.className("task-manager-organization-notificaation"))
            .stream()
            .map(Notification::new)
            .toList();
    }
}
