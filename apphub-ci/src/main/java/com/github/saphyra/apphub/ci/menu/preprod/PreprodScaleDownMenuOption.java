package com.github.saphyra.apphub.ci.menu.preprod;

import com.github.saphyra.apphub.ci.localization.LocalizationProvider;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.Menu;
import com.github.saphyra.apphub.ci.menu.MenuOption;
import com.github.saphyra.apphub.ci.menu.MenuOrder;
import com.github.saphyra.apphub.ci.menu.MenuOrderEnum;
import com.github.saphyra.apphub.ci.process.minikube.preprod.PreprodScaleDownProcess;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class PreprodScaleDownMenuOption implements MenuOption {
    private final PreprodScaleDownProcess preprodScaleDownProcess;

    @Override
    public Menu getMenu() {
        return Menu.PREPROD_MENU;
    }

    @Override
    public MenuOrder getOrder() {
        return MenuOrderEnum.DELETE_NAMESPACE;
    }

    @Override
    public LocalizationProvider getName() {
        return LocalizedText.SCALE_DOWN;
    }

    @Override
    public boolean process() {
        preprodScaleDownProcess.scaleDown();

        return false;
    }
}
