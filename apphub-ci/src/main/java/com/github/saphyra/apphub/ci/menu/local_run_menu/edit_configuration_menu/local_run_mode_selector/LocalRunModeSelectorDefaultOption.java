package com.github.saphyra.apphub.ci.menu.local_run_menu.edit_configuration_menu.local_run_mode_selector;

import com.github.saphyra.apphub.ci.dao.PropertyDao;
import com.github.saphyra.apphub.ci.dao.PropertyName;
import com.github.saphyra.apphub.ci.value.LocalRunMode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class LocalRunModeSelectorDefaultOption implements LocalRunModeSelectorOption {
    private final PropertyDao propertyDao;

    @Override
    public String getCommand() {
        return "1";
    }

    @Override
    public String getName() {
        return LocalRunMode.DEFAULT.name(); //TODO translate
    }

    @Override
    public boolean process() {
        propertyDao.save(PropertyName.LOCAL_RUN_MODE, LocalRunMode.DEFAULT.name());

        log.info("Local Run Mode updated."); //TODO translate

        return true;
    }
}
