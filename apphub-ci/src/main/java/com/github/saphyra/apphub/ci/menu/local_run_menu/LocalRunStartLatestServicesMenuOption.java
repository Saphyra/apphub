package com.github.saphyra.apphub.ci.menu.local_run_menu;

import com.github.saphyra.apphub.ci.dao.PropertyDao;
import com.github.saphyra.apphub.ci.localization.LocalizationProvider;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.Menu;
import com.github.saphyra.apphub.ci.menu.MenuOption;
import com.github.saphyra.apphub.ci.menu.MenuOrder;
import com.github.saphyra.apphub.ci.menu.MenuOrderEnum;
import com.github.saphyra.apphub.ci.process.local.start.LocalStartServicesProcess;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
class LocalRunStartLatestServicesMenuOption implements MenuOption {
    private final PropertyDao propertyDao;
    private final LocalStartServicesProcess localStartServicesProcess;

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
        return MenuOrderEnum.DEPLOY_LATEST_SERVICES;
    }

    @Override
    public LocalizationProvider getName() {
        String latestServices = String.join(", ", propertyDao.getLatestServices());

        return language -> LocalizedText.LOCAL_START_LATEST_SERVICES.getLocalizedText(language).formatted(latestServices);
    }

    @Override
    public boolean process() {
        localStartServicesProcess.startLatestServices();

        return false;
    }
}
