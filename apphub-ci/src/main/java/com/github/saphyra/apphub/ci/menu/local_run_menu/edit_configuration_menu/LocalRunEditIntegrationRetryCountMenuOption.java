package com.github.saphyra.apphub.ci.menu.local_run_menu.edit_configuration_menu;

import com.github.saphyra.apphub.ci.dao.PropertyDao;
import com.github.saphyra.apphub.ci.dao.PropertyName;
import com.github.saphyra.apphub.ci.localization.LocalizationProvider;
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
public class LocalRunEditIntegrationRetryCountMenuOption implements MenuOption {
    private final PropertyDao propertyDao;
    private final ValidatingInputReader validatingInputReader;

    @Override
    public Menu getMenu() {
        return Menu.LOCAL_RUN_EDIT_CONFIGURATION_MENU;
    }

    @Override
    public MenuOrder getOrder() {
        return MenuOrderEnum.SETTINGS_INTEGRATION_RETRY_COUNT;
    }

    @Override
    public LocalizationProvider getName() {
        return language -> LocalizedText.INTEGRATION_RETRY_COUNT.getLocalizedText(language).formatted(propertyDao.getLocalIntegrationRetryCount());
    }

    @Override
    public boolean process() {
        Integer threadCount = validatingInputReader.getInput(
            LocalizedText.INTEGRATION_RETRY_COUNT_LABEL,
            Integer::parseInt,
            tc -> Optional.empty()
        );

        propertyDao.save(PropertyName.LOCAL_INTEGRATION_RETRY_COUNT, String.valueOf(threadCount));

        return false;
    }
}
