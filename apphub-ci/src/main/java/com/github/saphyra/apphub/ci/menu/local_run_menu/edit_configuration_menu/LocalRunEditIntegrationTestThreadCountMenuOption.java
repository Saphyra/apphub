package com.github.saphyra.apphub.ci.menu.local_run_menu.edit_configuration_menu;

import com.github.saphyra.apphub.ci.dao.PropertyDao;
import com.github.saphyra.apphub.ci.dao.PropertyName;
import com.github.saphyra.apphub.ci.utils.ValidatingInputReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
class LocalRunEditIntegrationTestThreadCountMenuOption implements LocalRunEditPropertiesMenuOption {
    private final PropertyDao propertyDao;
    private final ValidatingInputReader validatingInputReader;

    @Override
    public String getCommand() {
        return "3";
    }

    @Override
    public String getName() {
        return "Integration Test Thread Count (%s)".formatted(propertyDao.getLocalRunTestsThreadCount()); //TODO translate
    }

    @Override
    public boolean process() {
        Integer threadCount = validatingInputReader.getInput(
            "Thread Count for running integration tests:", //TODO translate
            Integer::parseInt,
            tc -> {
                if (tc < 1) {
                    return Optional.of("Must not be lower than 1"); //TODO translate
                }

                return Optional.empty();
            }
        );

        propertyDao.save(PropertyName.LOCAL_RUN_INTEGRATION_TESTS_THREAD_COUNT, String.valueOf(threadCount));

        log.info("Thread Count updated."); //TODO translate

        return false;
    }
}
