package com.github.saphyra.apphub.ci.process.local.start;

import com.github.saphyra.apphub.ci.dao.PropertyDao;
import com.github.saphyra.apphub.ci.dao.PropertyName;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.ServiceListValidator;
import com.github.saphyra.apphub.ci.process.ProcessKiller;
import com.github.saphyra.apphub.ci.utils.ObjectMapperWrapper;
import com.github.saphyra.apphub.ci.utils.ValidatingInputReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
@RequiredArgsConstructor
@Slf4j
public class LocalStartServicesProcess {
    private final ValidatingInputReader validatingInputReader;
    private final LocalBuildTask localBuildTask;
    private final ProcessKiller processKiller;
    private final ServiceStarter serviceStarter;
    private final PropertyDao propertyDao;
    private final ObjectMapperWrapper objectMapperWrapper;
    private final ServiceListValidator serviceListValidator;

    public void start() {
        String[] servicesToStartArr = validatingInputReader.getInput(
            LocalizedText.SERVICES_TO_START,
            input -> isBlank(input) ? new String[0] : input.split(","),
            serviceListValidator
        );

        if (servicesToStartArr.length == 0) {
            return;
        }

        List<String> servicesToStart = Arrays.asList(servicesToStartArr);

        startServices(servicesToStart);

        propertyDao.save(PropertyName.LATEST_SERVICES, objectMapperWrapper.writeValueAsString(servicesToStart));
    }

    public void startLatestServices() {
        startServices(propertyDao.getLatestServices());
    }

    private void startServices(List<String> servicesToStart) {
        servicesToStart.forEach(processKiller::killByServiceName);

        if (!localBuildTask.buildServices(servicesToStart)) {
            log.error("Build failed. Startup sequence stopped.");
            return;
        }

        serviceStarter.startServices(servicesToStart);
    }
}
