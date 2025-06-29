package com.github.saphyra.apphub.ci.menu.minikube;

import com.github.saphyra.apphub.ci.dao.PropertyDao;
import com.github.saphyra.apphub.ci.localization.LocalizationProvider;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.Menu;
import com.github.saphyra.apphub.ci.menu.MenuOption;
import com.github.saphyra.apphub.ci.menu.MenuOrderEnum;
import com.github.saphyra.apphub.ci.process.minikube.local.MinikubeLocalRunTestsProcess;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
class MinikubeRunLatestTestGroupsMenuOption implements MenuOption {
    private final MinikubeLocalRunTestsProcess minikubeLocalRunTestsProcess;
    private final PropertyDao propertyDao;

    @Override
    public Menu getMenu() {
        String latestTestGroups = propertyDao.getLatestTestGroups();

        if (isNull(latestTestGroups)) {
            return Menu.NONE;
        }

        return Menu.MINIKUBE_MENU;
    }

    @Override
    public MenuOrderEnum getOrder() {
        return MenuOrderEnum.RUN_LATEST_TEST_GROUPS;
    }

    @Override
    public LocalizationProvider getName() {
        String latestServices = String.join(", ", propertyDao.getLatestTestGroups());

        return language -> LocalizedText.RUN_LATEST_TEST_GROUPS.getLocalizedText(language).formatted(latestServices);
    }

    @Override
    public boolean process() {
        minikubeLocalRunTestsProcess.runTests(propertyDao.getLatestTestGroups());

        return false;
    }
}
