package com.github.saphyra.apphub.ci.menu.minikube.edit_configuration_menu.mode_selector;

import com.github.saphyra.apphub.ci.dao.PropertyDao;
import com.github.saphyra.apphub.ci.dao.PropertyName;
import com.github.saphyra.apphub.ci.localization.LocalizationService;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.Menu;
import com.github.saphyra.apphub.ci.menu.MenuOption;
import com.github.saphyra.apphub.ci.menu.MenuOrderEnum;
import com.github.saphyra.apphub.ci.value.DeployMode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class MinikubeModeSelectorSkipBuildOptionOption implements MenuOption {
    private final PropertyDao propertyDao;
    private final LocalizationService localizationService;

    @Override
    public Menu getMenu() {
        return Menu.MINIKUBE_MODE_SELECTOR_MENU;
    }

    @Override
    public MenuOrderEnum getOrder() {
        return MenuOrderEnum.SETTINGS_DEPLOY_MODE_SKIP_BUILD;
    }

    @Override
    public LocalizedText getName() {
        return LocalizedText.DEPLOY_MODE_SKIP_BUILD;
    }

    @Override
    public boolean process() {
        propertyDao.save(PropertyName.REMOTE_DEPLOY_MODE, DeployMode.SKIP_BUILD.name());

        localizationService.writeMessage(LocalizedText.DEPLOY_MODE_UPDATED);

        return true;
    }
}
