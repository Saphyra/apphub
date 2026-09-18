package com.github.saphyra.apphub.ci.service.deployment.stop;

import com.github.saphyra.apphub.ci.service.deployment.stop.local.LocalStopService;
import com.github.saphyra.apphub.ci.value.Environment;
import com.github.saphyra.apphub.ci.value.Service;
import com.github.saphyra.apphub.ci.value.Services;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class StopFacade {
    private final LocalStopService localStopService;
    private final Services services;

    public void stop(Environment environment, boolean stopUserDefinedServices, List<String> userDefinedServiceNames){
        switch (environment){
            case LOCAL -> localStopService.stopServices(getServicesToStop(stopUserDefinedServices, userDefinedServiceNames));
            default -> throw new IllegalArgumentException("Unsupported environment: " + environment);
        }
    }

    private List<Service> getServicesToStop(boolean stopUserDefinedServices, List<String> userDefinedServiceNames) {
        if (stopUserDefinedServices) {
            return services.getServices()
                .stream()
                .filter(service -> userDefinedServiceNames.contains(service.getName()))
                .toList();

        } else {
            return services.getServices();
        }
    }
}
