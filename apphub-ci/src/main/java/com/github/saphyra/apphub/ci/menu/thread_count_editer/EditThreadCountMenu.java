package com.github.saphyra.apphub.ci.menu.thread_count_editer;

import com.github.saphyra.apphub.ci.localization.LocalizationService;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.Menu;
import com.github.saphyra.apphub.ci.menu.MenuBase;
import com.github.saphyra.apphub.ci.menu.MenuOption;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EditThreadCountMenu extends MenuBase {
    EditThreadCountMenu(List<MenuOption> menuOptions, LocalizationService localizationService) {
        super(menuOptions, localizationService, Menu.EDIT_THREAD_COUNT_MENU);
    }

    @Override
    protected LocalizedText getName() {
        return LocalizedText.THREAD_COUNT;
    }
}
