package com.github.saphyra.apphub.ci.menu.main_menu;

import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.local_run_menu.LocalRunMenu;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class MainMenuLocalRunOption implements MainMenuOption {
    private final LocalRunMenu localRunMenu;

    @Override
    public String getCommand() {
        return "1";
    }

    @Override
    public LocalizedText getName() {
        return LocalizedText.LOCAL_RUN;
    }

    @Override
    public boolean process() {
        localRunMenu.enter();

        return false;
    }
}
