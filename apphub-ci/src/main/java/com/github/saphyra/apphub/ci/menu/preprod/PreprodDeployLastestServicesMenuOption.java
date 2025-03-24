package com.github.saphyra.apphub.ci.menu.preprod;

import com.github.saphyra.apphub.ci.dao.PropertyDao;
import com.github.saphyra.apphub.ci.localization.LocalizationProvider;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.Menu;
import com.github.saphyra.apphub.ci.menu.MenuOption;
import com.github.saphyra.apphub.ci.menu.MenuOrder;
import com.github.saphyra.apphub.ci.menu.MenuOrderEnum;
import com.github.saphyra.apphub.ci.process.minikube.preprod.PreprodReleaseProcess;
import com.github.saphyra.apphub.ci.utils.ValidatingInputReader;
import com.github.saphyra.apphub.ci.value.Services;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class PreprodDeployLastestServicesMenuOption implements MenuOption {
    private final ValidatingInputReader validatingInputReader;
    private final PreprodReleaseProcess preprodReleaseProcess;
    private final Services services;
    private final PropertyDao propertyDao;

    @Override
    public Menu getMenu() {
        return Menu.PREPROD_MENU;
    }

    @Override
    public MenuOrder getOrder() {
        return MenuOrderEnum.DEPLOY_LATEST_SERVICES;
    }

    @Override
    public LocalizationProvider getName() {
        String latestServices = String.join(", ", propertyDao.getLatestServices());

        return language -> LocalizedText.PREPROD_DEPLOY__LATEST_SERVICES.getLocalizedText(language).formatted(latestServices);
    }

    @Override
    public boolean process() {
        preprodReleaseProcess.deployServices(propertyDao.getLatestServices());

        return false;
    }
}
