package com.github.saphyra.apphub.ci.menu.main_menu;

import com.github.saphyra.apphub.ci.localization.LocalizationProvider;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.minikube.MinikubeMenu;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class MainMenuMinikubeMenuOption implements MainMenuOption {
    private final MinikubeMenu minikubeMenu;

    @Override
    public String getCommand() {
        return "3";
    }

    @Override
    public LocalizationProvider getName() {
        return LocalizedText.MINIKUBE;
    }

    @Override
    public boolean process() {
        minikubeMenu.enter();

        return false;
    }
}
