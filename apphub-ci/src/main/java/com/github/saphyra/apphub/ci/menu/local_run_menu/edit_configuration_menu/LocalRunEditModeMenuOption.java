package com.github.saphyra.apphub.ci.menu.local_run_menu.edit_configuration_menu;

import com.github.saphyra.apphub.ci.dao.PropertyDao;
import com.github.saphyra.apphub.ci.localization.LocalizationProvider;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.Menu;
import com.github.saphyra.apphub.ci.menu.MenuOption;
import com.github.saphyra.apphub.ci.menu.MenuOrder;
import com.github.saphyra.apphub.ci.menu.MenuOrderEnum;
import com.github.saphyra.apphub.ci.menu.local_run_menu.edit_configuration_menu.mode_selector.LocalRunModeSelectorMenu;
import com.github.saphyra.apphub.ci.utils.ApplicationContextProxy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class LocalRunEditModeMenuOption implements MenuOption {
    private final ApplicationContextProxy applicationContextProxy;
    private final PropertyDao propertyDao;

    @Override
    public Menu getMenu() {
        return Menu.LOCAL_RUN_EDIT_CONFIGURATION_MENU;
    }

    @Override
    public MenuOrder getOrder() {
        return MenuOrderEnum.SETTINGS_DEPLOY_MODE;
    }

    @Override
    public LocalizationProvider getName() {
        return language -> LocalizedText.DEPLOY_MODE.getLocalizedText(language).formatted(propertyDao.getLocalDeployMode().getLocalization().getLocalizedText(language).replace(" (%s)", ""));
    }

    @Override
    public boolean process() {
        applicationContextProxy.getBean(LocalRunModeSelectorMenu.class).enter();

        return false;
    }
}
