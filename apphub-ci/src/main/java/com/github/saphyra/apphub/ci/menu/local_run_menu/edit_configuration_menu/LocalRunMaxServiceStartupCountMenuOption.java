package com.github.saphyra.apphub.ci.menu.local_run_menu.edit_configuration_menu;

import com.github.saphyra.apphub.ci.dao.PropertyDao;
import com.github.saphyra.apphub.ci.dao.PropertyName;
import com.github.saphyra.apphub.ci.localization.LocalizationProvider;
import com.github.saphyra.apphub.ci.localization.LocalizationService;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.utils.ValidatingInputReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
class LocalRunMaxServiceStartupCountMenuOption implements LocalRunEditConfigurationMenuOption {
    private final PropertyDao propertyDao;
    private final ValidatingInputReader validatingInputReader;
    private final LocalizationService localizationService;

    @Override
    public String getCommand() {
        return "4";
    }

    @Override
    public LocalizationProvider getName() {
        return language -> LocalizedText.SERVICE_STARTUP_COUNT_LIMIT.getLocalizedText(language).formatted(propertyDao.getStartupCountLimit());
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
