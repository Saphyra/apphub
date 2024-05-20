package com.github.saphyra.apphub.ci.menu.minikube.edit_configuration_menu;

import com.github.saphyra.apphub.ci.dao.PropertyDao;
import com.github.saphyra.apphub.ci.localization.LocalizationProvider;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.minikube.edit_configuration_menu.mode_selector.MinikubeModeSelectorMenu;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class MinikubeEditModeMenuOption implements MinikubeEditConfigurationMenuOption {
    private final MinikubeModeSelectorMenu minikubeModeSelectorMenu;
    private final PropertyDao propertyDao;

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
        minikubeModeSelectorMenu.enter();

        return false;
    }
}
