package com.github.saphyra.apphub.ci.menu.local_run_menu;

import com.github.saphyra.apphub.ci.dao.PropertyDao;
import com.github.saphyra.apphub.ci.dao.PropertyName;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.Menu;
import com.github.saphyra.apphub.ci.menu.MenuOption;
import com.github.saphyra.apphub.ci.menu.MenuOrderEnum;
import com.github.saphyra.apphub.ci.process.local.run_tests.LocalRunTestsProcess;
import com.github.saphyra.apphub.ci.utils.ValidatingInputReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
@RequiredArgsConstructor
class LocalRunTestGroupsMenuOption implements MenuOption {
    private final ValidatingInputReader validatingInputReader;
    private final LocalRunTestsProcess localRunTestsProcess;
    private final PropertyDao propertyDao;

    @Override
    public Menu getMenu() {
        return Menu.LOCAL_RUN_MENU;
    }

    @Override
    public MenuOrderEnum getOrder() {
        return MenuOrderEnum.RUN_TEST_GROUPS;
    }

    @Override
    public LocalizedText getName() {
        return LocalizedText.RUN_TEST_GROUPS;
    }

    @Override
    public boolean process() {
        String testGroups = validatingInputReader.getInput(
            LocalizedText.WHICH_TEST_GROUPS_TO_RUN,
            s -> Optional.empty()
        );

        if (isBlank(testGroups)) {
            return false;
        }

        propertyDao.save(PropertyName.LATEST_TEST_GROUPS, testGroups);

        localRunTestsProcess.run(testGroups);

        return false;
    }
}
