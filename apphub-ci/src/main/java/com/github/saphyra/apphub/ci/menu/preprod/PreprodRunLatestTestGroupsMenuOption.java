package com.github.saphyra.apphub.ci.menu.preprod;

import com.github.saphyra.apphub.ci.dao.PropertyDao;
import com.github.saphyra.apphub.ci.localization.LocalizationProvider;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.Menu;
import com.github.saphyra.apphub.ci.menu.MenuOption;
import com.github.saphyra.apphub.ci.menu.MenuOrderEnum;
import com.github.saphyra.apphub.ci.process.minikube.preprod.PreprodRunTestsProcess;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
class PreprodRunLatestTestGroupsMenuOption implements MenuOption {
    private final PreprodRunTestsProcess preprodRunTestsProcess;
    private final PropertyDao propertyDao;

    @Override
    public Menu getMenu() {
        String latestTestGroups = propertyDao.getLatestTestGroups();

        if (isNull(latestTestGroups)) {
            return Menu.NONE;
        }

        return Menu.PREPROD_MENU;
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
        preprodRunTestsProcess.runTests(propertyDao.getLatestTestGroups());

        return false;
    }
}
