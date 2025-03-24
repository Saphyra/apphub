package com.github.saphyra.apphub.ci.menu.preprod;

import com.github.saphyra.apphub.ci.dao.PropertyDao;
import com.github.saphyra.apphub.ci.dao.PropertyName;
import com.github.saphyra.apphub.ci.localization.LocalizationProvider;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.Menu;
import com.github.saphyra.apphub.ci.menu.MenuOption;
import com.github.saphyra.apphub.ci.menu.MenuOrder;
import com.github.saphyra.apphub.ci.menu.MenuOrderEnum;
import com.github.saphyra.apphub.ci.process.minikube.preprod.PreprodReleaseProcess;
import com.github.saphyra.apphub.ci.utils.ObjectMapperWrapper;
import com.github.saphyra.apphub.ci.utils.ValidatingInputReader;
import com.github.saphyra.apphub.ci.value.Services;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
class PreprodDeployServiceMenuOption implements MenuOption {
    private final ValidatingInputReader validatingInputReader;
    private final PreprodReleaseProcess preprodReleaseProcess;
    private final Services services;
    private final PropertyDao propertyDao;
    private final ObjectMapperWrapper objectMapperWrapper;

    @Override
    public Menu getMenu() {
        return Menu.PREPROD_MENU;
    }

    @Override
    public MenuOrder getOrder() {
        return MenuOrderEnum.DEPLOY_SERVICES;
    }

    @Override
    public LocalizationProvider getName() {
        return LocalizedText.PREPROD_DEPLOY_SERVICES;
    }

    @Override
    public boolean process() {
        String[] servicesToStartArr = validatingInputReader.getInput(
            LocalizedText.SERVICES_TO_START,
            input -> input.split(","),
            input -> Arrays.stream(input)
                .filter(serviceName -> services.getServices().stream().noneMatch(service -> service.getName().equals(serviceName)))
                .findAny()
                .map(serviceName -> language -> LocalizedText.SERVICE_NOT_FOUND.getLocalizedText(language).formatted(serviceName))
        );


        List<String> serviceNames = Arrays.asList(servicesToStartArr);
        preprodReleaseProcess.deployServices(serviceNames);

        propertyDao.save(PropertyName.LATEST_SERVICES, objectMapperWrapper.writeValueAsString(serviceNames));

        return false;
    }
}
