package com.github.saphyra.apphub.ci.menu.minikube;

import com.github.saphyra.apphub.ci.localization.LocalizationProvider;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.minikube.edit_configuration_menu.MinikubeEditConfigurationMenu;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class MinikubeEditConfigurationMenuOption implements MinikubeMenuOption {
    private final MinikubeEditConfigurationMenu minikubeEditConfigurationMenu;

    @Override
    public String getCommand() {
        return "4";
    }

    @Override
    public LocalizationProvider getName() {
        return LocalizedText.EDIT_CONFIGURATION;
    }

    @Override
    public boolean process() {
        minikubeEditConfigurationMenu.enter();

        return false;
    }
}
