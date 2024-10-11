package com.github.saphyra.apphub.ci.menu.minikube.edit_configuration_menu;

import com.github.saphyra.apphub.ci.dao.PropertyDao;
import com.github.saphyra.apphub.ci.dao.PropertyName;
import com.github.saphyra.apphub.ci.localization.LocalizationProvider;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.Menu;
import com.github.saphyra.apphub.ci.menu.MenuOption;
import com.github.saphyra.apphub.ci.menu.MenuOrderEnum;
import com.github.saphyra.apphub.ci.utils.ValidatingInputReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
class MinikubeRunPreCreateDriversMenuOption implements MenuOption {
    private final PropertyDao propertyDao;
    private final ValidatingInputReader validatingInputReader;

    @Override
    public Menu getMenu() {
        throw new UnsupportedOperationException("This should not be called");
    }

    @Override
    public List<Menu> getMenus() {
        return List.of(Menu.MINIKUBE_EDIT_CONFIGURATION_MENU, Menu.PREPROD_EDIT_CONFIGURATION_MENU);
    }

    @Override
    public MenuOrderEnum getOrder() {
        return MenuOrderEnum.SETTINGS_INTEGRATION_PRE_CREATE_DRIVERS;
    }

    @Override
    public LocalizationProvider getName() {
        return language -> LocalizedText.PRE_CREATE_DRIVERS.getLocalizedText(language).formatted(propertyDao.getRemoteRunPreCreateDriverCount());
    }

    @Override
    public boolean process() {
        Integer count = validatingInputReader.getInput(
            LocalizedText.PRE_CREATE_DRIVERS_COUNT,
            Integer::parseInt,
            tc -> {
                if (tc < 1) {
                    return Optional.of(LocalizedText.MUST_NOT_BE_LOWER_THAN_1);
                }

                return Optional.empty();
            }
        );

        propertyDao.save(PropertyName.REMOTE_RUN_TESTS_PRE_CREATE_DRIVER_COUNT, String.valueOf(count));

        return false;
    }
}
