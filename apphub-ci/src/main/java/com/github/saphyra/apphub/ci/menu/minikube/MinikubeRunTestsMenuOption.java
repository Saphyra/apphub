package com.github.saphyra.apphub.ci.menu.minikube;

import com.github.saphyra.apphub.ci.localization.LocalizationProvider;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.Menu;
import com.github.saphyra.apphub.ci.menu.MenuOption;
import com.github.saphyra.apphub.ci.process.minikube.local.MinikubeLocalRunTestsProcess;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class MinikubeRunTestsMenuOption implements MenuOption {
    private final MinikubeLocalRunTestsProcess minikubeLocalRunTestsProcess;

    @Override
    public Menu getMenu() {
        return Menu.MINIKUBE_MENU;
    }

    @Override
    public String getCommand() {
        return "5";
    }

    @Override
    public LocalizationProvider getName() {
        return LocalizedText.RUN_TESTS;
    }

    @Override
    public boolean process() {
        minikubeLocalRunTestsProcess.runTests();

        return false;
    }
}
