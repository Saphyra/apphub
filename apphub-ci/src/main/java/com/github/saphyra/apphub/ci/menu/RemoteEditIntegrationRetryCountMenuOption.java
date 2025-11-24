package com.github.saphyra.apphub.ci.menu;

import com.github.saphyra.apphub.ci.dao.PropertyDao;
import com.github.saphyra.apphub.ci.dao.PropertyName;
import com.github.saphyra.apphub.ci.localization.LocalizationProvider;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.utils.ValidatingInputReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RemoteEditIntegrationRetryCountMenuOption implements MenuOption {
    private final PropertyDao propertyDao;
    private final ValidatingInputReader validatingInputReader;

    @Override
    public Menu getMenu() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Menu> getMenus() {
        return List.of(Menu.MINIKUBE_EDIT_CONFIGURATION_MENU, Menu.PREPROD_EDIT_CONFIGURATION_MENU, Menu.PRODUCTION_EDIT_CONFIGURATIONS_MENU);
    }

    @Override
    public MenuOrder getOrder() {
        return MenuOrderEnum.SETTINGS_INTEGRATION_RETRY_COUNT;
    }

    @Override
    public LocalizationProvider getName() {
        return language -> LocalizedText.INTEGRATION_RETRY_COUNT.getLocalizedText(language).formatted(propertyDao.getRemoteIntegrationRetryCount());
    }

    @Override
    public boolean process() {
        Integer threadCount = validatingInputReader.getInput(
            LocalizedText.INTEGRATION_RETRY_COUNT_LABEL,
            Integer::parseInt,
            tc -> Optional.empty()
        );

        propertyDao.save(PropertyName.REMOTE_INTEGRATION_RETRY_COUNT, String.valueOf(threadCount));

        return false;
    }
}
