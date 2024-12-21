package com.github.saphyra.apphub.ci.menu.preprod;

import com.github.saphyra.apphub.ci.localization.LocalizationProvider;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.Menu;
import com.github.saphyra.apphub.ci.menu.MenuOption;
import com.github.saphyra.apphub.ci.menu.MenuOrderEnum;
import com.github.saphyra.apphub.ci.process.minikube.preprod.StartPreprodProxyProcess;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class StartPreprodProxyMenuOption implements MenuOption {
    private final StartPreprodProxyProcess preprodProxyProcess;

    @Override
    public Menu getMenu() {
        return Menu.PREPROD_MENU;
    }

    @Override
    public MenuOrderEnum getOrder() {
        return MenuOrderEnum.APPHUB_PROXY;
    }

    @Override
    public LocalizationProvider getName() {
        return LocalizedText.PREPROD_PROXY;
    }

    @Override
    public boolean process() {
        preprodProxyProcess.startPreprodProxy();

        return false;
    }
}
