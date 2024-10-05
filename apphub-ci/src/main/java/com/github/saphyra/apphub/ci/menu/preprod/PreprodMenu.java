package com.github.saphyra.apphub.ci.menu.preprod;

import com.github.saphyra.apphub.ci.localization.LocalizationProvider;
import com.github.saphyra.apphub.ci.localization.LocalizationService;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.Menu;
import com.github.saphyra.apphub.ci.menu.MenuBase;
import com.github.saphyra.apphub.ci.menu.MenuOption;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PreprodMenu extends MenuBase {
    public PreprodMenu(List<MenuOption> options, LocalizationService localizationService) {
        super(options, localizationService, Menu.PREPROD_MENU);
    }

    @Override
    protected LocalizationProvider getName() {
        return LocalizedText.PREPROD;
    }
}
