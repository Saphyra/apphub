package com.github.saphyra.apphub.ci.menu.main_menu;

import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.Menu;
import com.github.saphyra.apphub.ci.menu.MenuOption;
import com.github.saphyra.apphub.ci.menu.MenuOrder;
import com.github.saphyra.apphub.ci.menu.MenuOrderEnum;
import com.github.saphyra.apphub.ci.menu.local_run_menu.LocalRunMenu;
import com.github.saphyra.apphub.ci.utils.ApplicationContextProxy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class MainMenuLocalRunOption implements MenuOption {
    private final ApplicationContextProxy applicationContextProxy;

    @Override
    public Menu getMenu() {
        return Menu.MAIN_MENU;
    }

    @Override
    public MenuOrder getOrder() {
        return MenuOrderEnum.MAIN_MENU_LOCAL_RUN;
    }

    @Override
    public LocalizedText getName() {
        return LocalizedText.LOCAL_RUN;
    }

    @Override
    public boolean process() {
        applicationContextProxy.getBean(LocalRunMenu.class).enter();

        return false;
    }
}
