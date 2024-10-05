package com.github.saphyra.apphub.ci.menu.minikube.edit_configuration_menu;

import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.Menu;
import com.github.saphyra.apphub.ci.menu.MenuOption;
import com.github.saphyra.apphub.ci.menu.thread_count_editer.EditThreadCountMenu;
import com.github.saphyra.apphub.ci.utils.ApplicationContextProxy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
class MinikubeEditBuildThreadCountMenuOption implements MenuOption {
    private final ApplicationContextProxy applicationContextProxy;

    @Override
    public Menu getMenu() {
        throw new UnsupportedOperationException("This should not be called");
    }

    @Override
    public List<Menu> getMenus() {
        return List.of(Menu.MINIKUBE_EDIT_CONFIGURATION_MENU, Menu.PREPROD_EDIT_CONFIGURATION_MENU);
    }

    @Override
    public String getCommand() {
        return "1";
    }

    @Override
    public LocalizedText getName() {
        return LocalizedText.BUILD_THREAD_COUNT;
    }

    @Override
    public boolean process() {
        applicationContextProxy.getBean(EditThreadCountMenu.class).enter();

        return false;
    }
}
