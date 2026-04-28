package com.github.saphyra.apphub.ci.menu.settings.s3;

import com.github.saphyra.apphub.ci.localization.LocalizationProvider;
import com.github.saphyra.apphub.ci.localization.LocalizationService;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.Menu;
import com.github.saphyra.apphub.ci.menu.MenuBase;
import com.github.saphyra.apphub.ci.menu.MenuOption;
import com.github.saphyra.apphub.ci.value.Environment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class S3SettingsMenu extends MenuBase {
    public S3SettingsMenu(LocalizationService localizationService, S3MenuContext context) {
        super(getMenuOptions(context), localizationService, Menu.S3_SETTINGS_MENU);
    }

    @Override
    protected LocalizationProvider getName() {
        return language -> LocalizedText.S3_SETTINGS.getLocalizedText(language) + " " + LocalizedText.SELECT_ENVIRONMENT.getLocalizedText(language);
    }

    private static List<MenuOption> getMenuOptions(S3MenuContext context) {
        List<MenuOption> result = new ArrayList<>();
        for (int order = 0; order < Environment.values().length; order++) {
            MenuOption menuOption = new S3EnvironmentOption(order + 1, context, Environment.values()[order]);
            result.add(menuOption);
        }

        return result;
    }
}
