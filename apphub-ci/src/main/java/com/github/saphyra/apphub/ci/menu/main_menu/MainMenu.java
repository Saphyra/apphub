package com.github.saphyra.apphub.ci.menu.main_menu;

import com.github.saphyra.apphub.ci.menu.MenuBase;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MainMenu extends MenuBase<MainMenuOption> {
    MainMenu(List<MainMenuOption> mainMenuOptions) {
        super(mainMenuOptions);
    }

    @Override
    protected String getName() {
        return "Main menu"; //TODO translate
    }
}
