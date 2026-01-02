package com.github.saphyra.apphub.ci.menu.local_run_menu.edit_configuration_menu;

import com.github.saphyra.apphub.ci.dao.PropertyDao;
import com.github.saphyra.apphub.ci.dao.PropertyName;
import com.github.saphyra.apphub.ci.localization.LocalizationProvider;
import com.github.saphyra.apphub.ci.localization.LocalizationService;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.Menu;
import com.github.saphyra.apphub.ci.menu.MenuOption;
import com.github.saphyra.apphub.ci.menu.MenuOrder;
import com.github.saphyra.apphub.ci.menu.MenuOrderEnum;
import com.github.saphyra.apphub.ci.utils.ValidatingInputReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
class LocalRunMaxServiceStartupCountMenuOption implements MenuOption {
    private final PropertyDao propertyDao;
    private final ValidatingInputReader validatingInputReader;
    private final LocalizationService localizationService;

    @Override
    public Menu getMenu() {
        return Menu.LOCAL_RUN_EDIT_CONFIGURATION_MENU;
    }

    @Override
    public MenuOrder getOrder() {
        return MenuOrderEnum.SETTINGS_MAX_SERVICE_STARTUP_COUNT;
    }

    @Override
    public LocalizationProvider getName() {
        return language -> LocalizedText.SERVICE_STARTUP_COUNT_LIMIT.getLocalizedText(language).formatted(propertyDao.getLocalStartupCountLimit());
    }

    @Override
    public boolean process() {
        Integer limit = validatingInputReader.getInput(
            language -> LocalizedText.SERVICE_STARTUP_COUNT_LIMIT.getLocalizedText(language).replace(" (%s)", ""),
            Integer::parseInt,
            tc -> {
                if (tc < 1) {
                    return Optional.of(LocalizedText.MUST_NOT_BE_LOWER_THAN_1);
                }

                return Optional.empty();
            }
        );

        propertyDao.save(PropertyName.LOCAL_RUN_SERVICE_STARTUP_COUNT_LIMIT, String.valueOf(limit));

        localizationService.writeMessage(LocalizedText.STARTUP_LIMIT_SAVED);

        return false;
    }
}
