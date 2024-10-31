package com.github.saphyra.apphub.ci.menu.local_run_menu.edit_configuration_menu.enable_services;

import com.github.saphyra.apphub.ci.dao.PropertyDao;
import com.github.saphyra.apphub.ci.dao.PropertyName;
import com.github.saphyra.apphub.ci.localization.Language;
import com.github.saphyra.apphub.ci.localization.LocalizationProvider;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.Menu;
import com.github.saphyra.apphub.ci.menu.MenuOption;
import com.github.saphyra.apphub.ci.menu.MenuOrder;
import com.github.saphyra.apphub.ci.utils.Utils;
import com.github.saphyra.apphub.ci.value.Service;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Builder
public class LocalRunEnableServicesMenuOption implements MenuOption {
    private final int index;
    private final Service service;
    private final PropertyDao propertyDao;

    @Override
    public Menu getMenu() {
        return Menu.LOCAL_RUN_EDIT_CONFIGURATION_ENABLE_SERVICE_MENU;
    }

    @Override
    public MenuOrder getOrder() {
        return () -> index;
    }

    @Override
    public LocalizationProvider getName() {
        return language -> getEnabledPrefix(language) + " " + service.getName();
    }

    private String getEnabledPrefix(Language language) {
        boolean enabled = isEnabled();

        String enabledText = LocalizedText.ENABLED.getLocalizedText(language);
        String disabledText = LocalizedText.DISABLED.getLocalizedText(language);

        String text = enabled ? enabled(enabledText, disabledText) : disabled(enabledText, disabledText);
        return "[%s]".formatted(text);
    }

    private boolean isEnabled() {
        return !propertyDao.getDisabledServices()
            .contains(service.getName());
    }

    private String enabled(String enabledText, String disabledText) {
        return Utils.withLeading(enabledText, Math.max(enabledText.length(), disabledText.length()), " ");
    }

    private String disabled(String enabledText, String disabledText) {
        return Utils.withLeading(disabledText, Math.max(enabledText.length(), disabledText.length()), " ");
    }

    @Override
    public boolean process() {
        List<String> disabledServices = propertyDao.getDisabledServices();
        if (isEnabled()) {
            disabledServices.add(service.getName());
        } else {
            disabledServices.remove(service.getName());
        }

        propertyDao.save(PropertyName.DISABLED_SERVICES, disabledServices);

        return false;
    }
}
