package com.github.saphyra.apphub.ci.menu.local_run_menu.edit_configuration_menu.local_run_mode_selector;

import com.github.saphyra.apphub.ci.dao.PropertyDao;
import com.github.saphyra.apphub.ci.dao.PropertyName;
import com.github.saphyra.apphub.ci.localization.LocalizationService;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.value.LocalRunMode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class LocalRunModeSelectorSkipBuildOptionOption implements LocalRunModeSelectorOption {
    private final PropertyDao propertyDao;
    private final LocalizationService localizationService;

    @Override
    public String getCommand() {
        return "3";
    }

    @Override
    public LocalizedText getName() {
        return LocalizedText.LOCAL_RUN_MODE_SKIP_BUILD;
    }

    @Override
    public boolean process() {
        propertyDao.save(PropertyName.LOCAL_RUN_MODE, LocalRunMode.SKIP_BUILD.name());

        localizationService.writeMessage(LocalizedText.LOCAL_RUN_MODE_UPDATED);

        return true;
    }
}
