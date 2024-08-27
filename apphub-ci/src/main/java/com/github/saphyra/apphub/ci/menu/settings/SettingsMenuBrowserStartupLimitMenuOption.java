package com.github.saphyra.apphub.ci.menu.settings;

import com.github.saphyra.apphub.ci.dao.PropertyDao;
import com.github.saphyra.apphub.ci.dao.PropertyName;
import com.github.saphyra.apphub.ci.localization.LocalizationProvider;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.Menu;
import com.github.saphyra.apphub.ci.menu.MenuOption;
import com.github.saphyra.apphub.ci.utils.ValidatingInputReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
class SettingsMenuBrowserStartupLimitMenuOption implements MenuOption {
    private final PropertyDao propertyDao;
    private final ValidatingInputReader validatingInputReader;

    @Override
    public Menu getMenu() {
        return Menu.SETTINGS_MENU;
    }

    @Override
    public String getCommand() {
        return "2";
    }

    @Override
    public LocalizationProvider getName() {
        return language -> LocalizedText.BROWSER_STARTUP_LIMIT_MENU_OPTION_LABEL.getLocalizedText(language).formatted(propertyDao.getBrowserStartupLimit());
    }

    @Override
    public boolean process() {
        Integer count = validatingInputReader.getInput(
            LocalizedText.BROWSER_STARTUP_LIMIT,
            Integer::parseInt,
            tc -> {
                if (tc < 1) {
                    return Optional.of(LocalizedText.MUST_NOT_BE_LOWER_THAN_1);
                }

                return Optional.empty();
            }
        );

        propertyDao.save(PropertyName.BROWSER_STARTUP_LIMIT, String.valueOf(count));

        return false;
    }
}
