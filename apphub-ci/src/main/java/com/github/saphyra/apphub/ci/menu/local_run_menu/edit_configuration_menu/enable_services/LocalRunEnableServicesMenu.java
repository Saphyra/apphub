package com.github.saphyra.apphub.ci.menu.local_run_menu.edit_configuration_menu.enable_services;

import com.github.saphyra.apphub.ci.dao.PropertyDao;
import com.github.saphyra.apphub.ci.localization.LocalizationProvider;
import com.github.saphyra.apphub.ci.localization.LocalizationService;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.Menu;
import com.github.saphyra.apphub.ci.menu.MenuBase;
import com.github.saphyra.apphub.ci.menu.MenuOption;
import com.github.saphyra.apphub.ci.value.Service;
import com.github.saphyra.apphub.ci.value.Services;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
public class LocalRunEnableServicesMenu extends MenuBase {
    private final Services services;
    private final PropertyDao propertyDao;

    LocalRunEnableServicesMenu(LocalizationService localizationService, Services services, PropertyDao propertyDao) {
        super(localizationService, Menu.LOCAL_RUN_EDIT_CONFIGURATION_ENABLE_SERVICE_MENU);
        this.services = services;
        this.propertyDao = propertyDao;
    }

    @Override
    protected LocalizationProvider getName() {
        return LocalizedText.TOGGLE_SERVICES;
    }

    @Override
    protected List<MenuOption> getOptions() {
        List<Service> ordered = services.getServices()
            .stream()
            .filter(Service::getOptional)
            .sorted(Comparator.comparing(Service::getName))
            .toList();

        List<MenuOption> result = new ArrayList<>();

        for (int i = 0; i < ordered.size(); i++) {
            MenuOption menuOption = LocalRunEnableServicesMenuOption.builder()
                .index(i + 1)
                .service(ordered.get(i))
                .propertyDao(propertyDao)
                .build();

            result.add(menuOption);
        }

        return result;
    }
}
