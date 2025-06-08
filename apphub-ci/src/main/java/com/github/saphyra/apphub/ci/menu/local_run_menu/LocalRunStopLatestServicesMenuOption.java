package com.github.saphyra.apphub.ci.menu.local_run_menu;

import com.github.saphyra.apphub.ci.dao.PropertyDao;
import com.github.saphyra.apphub.ci.localization.LocalizationProvider;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.Menu;
import com.github.saphyra.apphub.ci.menu.MenuOption;
import com.github.saphyra.apphub.ci.menu.MenuOrder;
import com.github.saphyra.apphub.ci.menu.MenuOrderEnum;
import com.github.saphyra.apphub.ci.process.local.stop.LocalStopProcess;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
class LocalRunStopLatestServicesMenuOption implements MenuOption {
    private final PropertyDao propertyDao;
    private final LocalStopProcess localStopProcess;

    @Override
    public Menu getMenu() {
        List<String> latestServices = propertyDao.getLatestServices();

        if (latestServices.isEmpty()) {
            return Menu.NONE;
        }

        return Menu.LOCAL_RUN_MENU;
    }

    @Override
    public MenuOrder getOrder() {
        return MenuOrderEnum.STOP_LATEST_SERVICES;
    }

    @Override
    public LocalizationProvider getName() {
        String latestServices = String.join(", ", propertyDao.getLatestServices());

        return language -> LocalizedText.LOCAL_STOP_LATEST_SERVICES.getLocalizedText(language).formatted(latestServices);
    }

    @Override
    public boolean process() {
        localStopProcess.stopLatestServices();

        return false;
    }
}
