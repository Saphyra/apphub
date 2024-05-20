package com.github.saphyra.apphub.ci.menu.local_run_menu.edit_configuration_menu.mode_selector;

import com.github.saphyra.apphub.ci.localization.LocalizationService;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.MenuBase;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LocalRunModeSelectorMenu extends MenuBase<LocalRunModeSelectorOption> {
    LocalRunModeSelectorMenu(List<LocalRunModeSelectorOption> localRunModeSelectorOptions, LocalizationService localizationService) {
        super(localRunModeSelectorOptions, localizationService);
    }

    @Override
    protected LocalizedText getName() {
        return LocalizedText.SELECT_MODE;
    }
}
