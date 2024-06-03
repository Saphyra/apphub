package com.github.saphyra.apphub.ci.menu.production.edit_configuration;

import com.github.saphyra.apphub.ci.dao.PropertyDao;
import com.github.saphyra.apphub.ci.dao.PropertyName;
import com.github.saphyra.apphub.ci.localization.LocalizationProvider;
import com.github.saphyra.apphub.ci.localization.LocalizationService;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.Menu;
import com.github.saphyra.apphub.ci.menu.MenuOption;
import com.github.saphyra.apphub.ci.utils.ValidatingInputReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
class ProductionEditIntegrationTestCountMenuOption implements MenuOption {
    private final PropertyDao propertyDao;
    private final ValidatingInputReader validatingInputReader;
    private final LocalizationService localizationService;

    @Override
    public Menu getMenu() {
        return Menu.PRODUCTION_EDIT_CONFIGURATIONS_MENU;
    }

    @Override
    public String getCommand() {
        return "1";
    }

    @Override
    public LocalizationProvider getName() {
        return language -> LocalizedText.INTEGRATION_TEST_THREAD_COUNT.getLocalizedText(language).formatted(propertyDao.getRemoteRunTestsThreadCount());
    }

    @Override
    public boolean process() {
        Integer threadCount = validatingInputReader.getInput(
            LocalizedText.THREAD_COUNT_FOR_RUNNING_INTEGRATION_TESTS,
            Integer::parseInt,
            tc -> {
                if (tc < 1) {
                    return Optional.of(LocalizedText.MUST_NOT_BE_LOWER_THAN_1);
                }

                return Optional.empty();
            }
        );

        propertyDao.save(PropertyName.REMOTE_INTEGRATION_TESTS_THREAD_COUNT, String.valueOf(threadCount));

        localizationService.writeMessage(LocalizedText.THREAD_COUNT_UPDATED);

        return false;
    }
}