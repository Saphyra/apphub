package com.github.saphyra.apphub.ci.menu.minikube.edit_configuration_menu.mode_selector;

import com.github.saphyra.apphub.ci.localization.LocalizationService;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.MenuBase;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MinikubeModeSelectorMenu extends MenuBase<MinikubeModeSelectorOption> {
    MinikubeModeSelectorMenu(List<MinikubeModeSelectorOption> minikubeModeSelectorOptions, LocalizationService localizationService) {
        super(minikubeModeSelectorOptions, localizationService);
    }

    @Override
    protected LocalizedText getName() {
        return LocalizedText.SELECT_MODE;
    }
}
