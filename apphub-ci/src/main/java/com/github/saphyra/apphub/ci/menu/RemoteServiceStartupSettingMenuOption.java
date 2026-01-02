package com.github.saphyra.apphub.ci.menu;

import com.github.saphyra.apphub.ci.dao.PropertyDao;
import com.github.saphyra.apphub.ci.dao.PropertyName;
import com.github.saphyra.apphub.ci.localization.LocalizationProvider;
import com.github.saphyra.apphub.ci.localization.LocalizationService;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.utils.ValidatingInputReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RemoteServiceStartupSettingMenuOption implements MenuOption {
    private final PropertyDao propertyDao;
    private final ValidatingInputReader validatingInputReader;
    private final LocalizationService localizationService;

    @Override
    public Menu getMenu() {
        throw new UnsupportedOperationException("This should not be called");
    }

    @Override
    public List<Menu> getMenus() {
        return List.of(Menu.MINIKUBE_EDIT_CONFIGURATION_MENU, Menu.PRODUCTION_EDIT_CONFIGURATIONS_MENU, Menu.PREPROD_EDIT_CONFIGURATION_MENU);
    }

    @Override
    public MenuOrder getOrder() {
        return MenuOrderEnum.SETTINGS_MAX_SERVICE_STARTUP_COUNT;
    }

    @Override
    public LocalizationProvider getName() {
        return language -> LocalizedText.SERVICE_STARTUP_COUNT_LIMIT.getLocalizedText(language).formatted(propertyDao.getRemoteStartupCountLimit());
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

        propertyDao.save(PropertyName.REMOTE_SERVICE_STARTUP_COUNT_LIMIT, String.valueOf(limit));

        localizationService.writeMessage(LocalizedText.STARTUP_LIMIT_SAVED);

        return false;
    }
}
