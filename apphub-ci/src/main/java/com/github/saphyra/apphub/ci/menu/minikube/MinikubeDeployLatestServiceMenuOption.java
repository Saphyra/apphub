package com.github.saphyra.apphub.ci.menu.minikube;

import com.github.saphyra.apphub.ci.dao.PropertyDao;
import com.github.saphyra.apphub.ci.localization.LocalizationProvider;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.Menu;
import com.github.saphyra.apphub.ci.menu.MenuOption;
import com.github.saphyra.apphub.ci.menu.MenuOrder;
import com.github.saphyra.apphub.ci.menu.MenuOrderEnum;
import com.github.saphyra.apphub.ci.process.minikube.local.MinikubeLocalDeployProcess;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
class MinikubeDeployLatestServiceMenuOption implements MenuOption {
    private final MinikubeLocalDeployProcess minikubeLocalDeployProcess;
    private final PropertyDao propertyDao;

    @Override
    public Menu getMenu() {
        List<String> latestServices = propertyDao.getLatestServices();

        if (latestServices.isEmpty()) {
            return Menu.NONE;
        }

        return Menu.MINIKUBE_MENU;
    }

    @Override
    public MenuOrder getOrder() {
        return MenuOrderEnum.DEPLOY_LATEST_SERVICES;
    }

    @Override
    public LocalizationProvider getName() {
        String latestServices = String.join(", ", propertyDao.getLatestServices());

        return language -> LocalizedText.REMOTE_DEPLOY_LATEST_SERVICES.getLocalizedText(language).formatted(latestServices);
    }

    @Override
    public boolean process() {
        minikubeLocalDeployProcess.deploy(propertyDao.getLatestServices());

        return false;
    }
}
