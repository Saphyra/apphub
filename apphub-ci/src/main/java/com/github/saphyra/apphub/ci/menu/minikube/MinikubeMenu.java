package com.github.saphyra.apphub.ci.menu.minikube;

import com.github.saphyra.apphub.ci.localization.LocalizationProvider;
import com.github.saphyra.apphub.ci.localization.LocalizationService;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.MenuBase;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MinikubeMenu extends MenuBase<MinikubeMenuOption> {
    public MinikubeMenu(List<MinikubeMenuOption> minikubeMenuOptions, LocalizationService localizationService) {
        super(minikubeMenuOptions, localizationService);
    }

    @Override
    protected LocalizationProvider getName() {
        return LocalizedText.MINIKUBE;
    }
}
