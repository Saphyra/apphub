package com.github.saphyra.apphub.ci.menu.local_run_menu;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class LocalRunMenuStartMenuOption implements LocalRunMenuOption {
    @Override
    public String getCommand() {
        return "2";
    }

    @Override
    public String getName() {
        return "Run"; //TODO translate
    }

    @Override
    public boolean process() {
        //TODO implement

        return false;
    }
}
