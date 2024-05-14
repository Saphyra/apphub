package com.github.saphyra.apphub.ci.menu.local_run_menu.edit_configuration_menu.local_run_mode_selector;

import com.github.saphyra.apphub.ci.value.LocalRunMode;
import com.github.saphyra.apphub.ci.dao.Property;
import com.github.saphyra.apphub.ci.dao.PropertyName;
import com.github.saphyra.apphub.ci.dao.PropertyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class LocalRunModeSelectorSkipBuildOptionOption implements LocalRunModeSelectorOption {
    private final PropertyRepository propertyRepository;

    @Override
    public String getCommand() {
        return "3";
    }

    @Override
    public String getName() {
        return LocalRunMode.SKIP_BUILD.name(); //TODO translate
    }

    @Override
    public boolean process() {
        propertyRepository.save(new Property(PropertyName.LOCAL_RUN_MODE, LocalRunMode.SKIP_BUILD.name()));

        log.info("Local Run Mode updated."); //TODO translate

        return true;
    }
}
