package com.github.saphyra.apphub.ci.menu.minikube.edit_configuration_menu;

import com.github.saphyra.apphub.ci.localization.LocalizationProvider;
import com.github.saphyra.apphub.ci.localization.LocalizationService;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.MenuBase;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MinikubeEditConfigurationMenu extends MenuBase<MinikubeEditConfigurationMenuOption> {
    public MinikubeEditConfigurationMenu(List<MinikubeEditConfigurationMenuOption> minikubeEditConfigurationMenuOptions, LocalizationService localizationService) {
        super(minikubeEditConfigurationMenuOptions, localizationService);
    }

    @Override
    protected LocalizationProvider getName() {
        return LocalizedText.WHICH_CONFIGURATION_TO_EDIT;
    }
}
