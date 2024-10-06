package com.github.saphyra.apphub.ci.menu.preprod;

import com.github.saphyra.apphub.ci.localization.LocalizationProvider;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.Menu;
import com.github.saphyra.apphub.ci.menu.MenuOption;
import com.github.saphyra.apphub.ci.process.minikube.preprod.PreprodStopProcess;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class PreprodStopMenuOption implements MenuOption {
    private final PreprodStopProcess preprodStopProcess;

    @Override
    public Menu getMenu() {
        return Menu.PREPROD_MENU;
    }

    @Override
    public String getCommand() {
        return "4";
    }

    @Override
    public LocalizationProvider getName() {
        return LocalizedText.MINIKUBE_STOP;
    }

    @Override
    public boolean process() {
        preprodStopProcess.stopMinikube();

        return false;
    }
}
