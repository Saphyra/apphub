package com.github.saphyra.apphub.ci.menu.local_run_menu;

import com.github.saphyra.apphub.ci.localization.LocalizationService;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.MenuBase;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LocalRunMenu extends MenuBase<LocalRunMenuOption> {
    LocalRunMenu(List<LocalRunMenuOption> localRunMenuOptions, LocalizationService localizationService) {
        super(localRunMenuOptions, localizationService);
    }

    @Override
    protected LocalizedText getName() {
        return LocalizedText.LOCAL_RUN;
    }
}