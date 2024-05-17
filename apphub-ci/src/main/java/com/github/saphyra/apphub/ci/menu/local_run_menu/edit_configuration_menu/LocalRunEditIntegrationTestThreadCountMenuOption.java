package com.github.saphyra.apphub.ci.menu.local_run_menu.edit_configuration_menu;

import com.github.saphyra.apphub.ci.dao.PropertyDao;
import com.github.saphyra.apphub.ci.dao.PropertyName;
import com.github.saphyra.apphub.ci.localization.LocalizationProvider;
import com.github.saphyra.apphub.ci.localization.LocalizationService;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.utils.ValidatingInputReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
class LocalRunEditIntegrationTestThreadCountMenuOption implements LocalRunEditPropertiesMenuOption {
    private final PropertyDao propertyDao;
    private final ValidatingInputReader validatingInputReader;
    private final LocalizationService localizationService;

    @Override
    public String getCommand() {
        return "3";
    }

    @Override
    public LocalizationProvider getName() {
        return language -> LocalizedText.INTEGRATION_TEST_THREAD_COUNT.getLocalizedText(language).formatted(propertyDao.getLocalRunTestsThreadCount());
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

        propertyDao.save(PropertyName.LOCAL_RUN_INTEGRATION_TESTS_THREAD_COUNT, String.valueOf(threadCount));

        localizationService.writeMessage(LocalizedText.THREAD_COUNT_UPDATED);

        return false;
    }
}
