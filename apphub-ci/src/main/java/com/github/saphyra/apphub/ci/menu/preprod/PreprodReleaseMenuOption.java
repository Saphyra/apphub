package com.github.saphyra.apphub.ci.menu.preprod;

import com.github.saphyra.apphub.ci.localization.LocalizationProvider;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.Menu;
import com.github.saphyra.apphub.ci.menu.MenuOption;
import com.github.saphyra.apphub.ci.menu.MenuOrderEnum;
import com.github.saphyra.apphub.ci.process.minikube.preprod.PreprodReleaseProcess;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class PreprodReleaseMenuOption implements MenuOption {
    private final PreprodReleaseProcess preprodReleaseProcess;

    @Override
    public Menu getMenu() {
        return Menu.PREPROD_MENU;
    }

    @Override
    public MenuOrderEnum getOrder() {
        return MenuOrderEnum.DEPLOY;
    }

    @Override
    public LocalizationProvider getName() {
        return LocalizedText.PREPROD_RELEASE;
    }

    @Override
    public boolean process() {
        preprodReleaseProcess.release();

        return false;
    }
}
