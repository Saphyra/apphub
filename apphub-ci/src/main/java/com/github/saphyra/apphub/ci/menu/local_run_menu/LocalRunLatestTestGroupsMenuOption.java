package com.github.saphyra.apphub.ci.menu.local_run_menu;

import com.github.saphyra.apphub.ci.dao.PropertyDao;
import com.github.saphyra.apphub.ci.localization.LocalizationProvider;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.Menu;
import com.github.saphyra.apphub.ci.menu.MenuOption;
import com.github.saphyra.apphub.ci.menu.MenuOrder;
import com.github.saphyra.apphub.ci.menu.MenuOrderEnum;
import com.github.saphyra.apphub.ci.process.local.run_tests.LocalRunTestsProcess;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
class LocalRunLatestTestGroupsMenuOption implements MenuOption {
    private final PropertyDao propertyDao;
    private final LocalRunTestsProcess localRunTestsProcess;

    @Override
    public Menu getMenu() {
        String latestTestGroups = propertyDao.getLatestTestGroups();

        if (isNull(latestTestGroups)) {
            return Menu.NONE;
        }

        return Menu.LOCAL_RUN_MENU;
    }

    @Override
    public MenuOrder getOrder() {
        return MenuOrderEnum.RUN_LATEST_TEST_GROUPS;
    }

    @Override
    public LocalizationProvider getName() {
        String latestServices = String.join(", ", propertyDao.getLatestTestGroups());

        return language -> LocalizedText.RUN_LATEST_TEST_GROUPS.getLocalizedText(language).formatted(latestServices);
    }

    @Override
    public boolean process() {
        localRunTestsProcess.run(propertyDao.getLatestTestGroups());

        return false;
    }
}
