package com.github.saphyra.apphub.ci.menu.minikube.edit_configuration_menu;

import com.github.saphyra.apphub.ci.dao.PropertyDao;
import com.github.saphyra.apphub.ci.localization.LocalizationProvider;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.Menu;
import com.github.saphyra.apphub.ci.menu.MenuOption;
import com.github.saphyra.apphub.ci.menu.minikube.edit_configuration_menu.mode_selector.MinikubeModeSelectorMenu;
import com.github.saphyra.apphub.ci.utils.ApplicationContextProxy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class MinikubeEditModeMenuOption implements MenuOption {
    private final ApplicationContextProxy applicationContextProxy;
    private final PropertyDao propertyDao;

    @Override
    public Menu getMenu() {
        return Menu.MINIKUBE_EDIT_CONFIGURATION_MENU;
    }

    @Override
    public String getCommand() {
        return "2";
    }

    @Override
    public LocalizationProvider getName() {
        return language -> LocalizedText.DEPLOY_MODE.getLocalizedText(language).formatted(propertyDao.getRemoteDeployMode().getLocalization().getLocalizedText(language).replace(" (%s)", ""));
    }

    @Override
    public boolean process() {
        applicationContextProxy.getBean(MinikubeModeSelectorMenu.class).enter();

        return false;
    }
}
