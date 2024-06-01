package com.github.saphyra.apphub.ci.menu.thread_count_editer;

import com.github.saphyra.apphub.ci.dao.PropertyDao;
import com.github.saphyra.apphub.ci.dao.PropertyName;
import com.github.saphyra.apphub.ci.localization.LocalizationProvider;
import com.github.saphyra.apphub.ci.localization.LocalizationService;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.Menu;
import com.github.saphyra.apphub.ci.menu.MenuOption;
import com.github.saphyra.apphub.ci.utils.ValidatingInputReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
class DefaultThreadCountEditerOption implements MenuOption {
    private final PropertyDao propertyDao;
    private final ValidatingInputReader validatingInputReader;
    private final LocalizationService localizationService;

    @Override
    public Menu getMenu() {
        return Menu.EDIT_THREAD_COUNT_MENU;
    }

    @Override
    public String getCommand() {
        return "1";
    }

    @Override
    public LocalizationProvider getName() {
        return language -> LocalizedText.DEPLOY_MODE_DEFAULT.getLocalizedText(language).formatted(propertyDao.getBuildThreadCountDefault());
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

        propertyDao.save(PropertyName.BUILD_THREAD_COUNT_DEFAULT, String.valueOf(threadCount));

        localizationService.writeMessage(LocalizedText.THREAD_COUNT_UPDATED);

        return false;
    }
}
