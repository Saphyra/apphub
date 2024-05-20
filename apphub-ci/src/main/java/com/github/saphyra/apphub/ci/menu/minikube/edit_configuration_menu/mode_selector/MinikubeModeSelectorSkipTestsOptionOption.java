package com.github.saphyra.apphub.ci.menu.minikube.edit_configuration_menu.mode_selector;

import com.github.saphyra.apphub.ci.dao.PropertyDao;
import com.github.saphyra.apphub.ci.dao.PropertyName;
import com.github.saphyra.apphub.ci.localization.LocalizationProvider;
import com.github.saphyra.apphub.ci.localization.LocalizationService;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.value.DeployMode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class MinikubeModeSelectorSkipTestsOptionOption implements MinikubeModeSelectorOption {
    private final PropertyDao propertyDao;
    private final LocalizationService localizationService;

    @Override
    public String getCommand() {
        return "2";
    }

    @Override
    public LocalizationProvider getName() {
        return language -> LocalizedText.DEPLOY_MODE_SKIP_TESTS.getLocalizedText(language).replace(" (%s)", "");
    }

    @Override
    public boolean process() {
        propertyDao.save(PropertyName.REMOTE_DEPLOY_MODE, DeployMode.SKIP_TESTS.name());

        localizationService.writeMessage(LocalizedText.DEPLOY_MODE_UPDATED);

        return true;
    }
}
