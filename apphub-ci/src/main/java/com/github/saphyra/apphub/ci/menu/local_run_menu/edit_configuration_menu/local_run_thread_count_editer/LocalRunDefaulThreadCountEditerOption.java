package com.github.saphyra.apphub.ci.menu.local_run_menu.edit_configuration_menu.local_run_thread_count_editer;

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

@Slf4j
@Component
@RequiredArgsConstructor
class LocalRunDefaulThreadCountEditerOption implements LocalRunThreadCountEditerMenuOption {
    private final PropertyDao propertyDao;
    private final ValidatingInputReader validatingInputReader;
    private final LocalizationService localizationService;

    @Override
    public String getCommand() {
        return "1";
    }

    @Override
    public LocalizationProvider getName() {
        return language -> LocalizedText.DEFAULT.getLocalizedText(language).formatted(propertyDao.getLocalRunThreadCountDefault());
    }

    @Override
    public boolean process() {
        Integer threadCount = validatingInputReader.getInput(
            LocalizedText.THREAD_COUNT_FOR_RUNNING_WITH_TESTS,
            Integer::parseInt,
            tc -> {
                if (tc < 1) {
                    return Optional.of(LocalizedText.MUST_NOT_BE_LOWER_THAN_1);
                }

                return Optional.empty();
            }
        );

        propertyDao.save(PropertyName.LOCAL_RUN_THREAD_COUNT_DEFAULT, String.valueOf(threadCount));

        localizationService.writeMessage(LocalizedText.THREAD_COUNT_UPDATED);

        return false;
    }
}
