package com.github.saphyra.apphub.ci.ui.startup;

import com.github.saphyra.apphub.ci.dao.PropertyDao;
import com.github.saphyra.apphub.ci.value.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class StartupIndicatorFactory {
    private final PropertyDao propertyDao;
    private final StartupIndicatorContext startupIndicatorContext;

    public StartupIndicator createFromServiceList(List<Service> servicesToStart) {
        List<String> serviceNames = servicesToStart.stream()
            .map(Service::getName)
            .toList();

        return createFromServiceNames(serviceNames);
    }

    public StartupIndicator createFromServiceNames(List<String> serviceNames) {
        return propertyDao.isGuiEnabled() ? new DefaultStartupIndicator(serviceNames, startupIndicatorContext).run() : noOpIndicator();
    }

    public StartupIndicator noOpIndicator() {
        return new NoOpStartupIndicator();
    }
}
