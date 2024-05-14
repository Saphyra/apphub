package com.github.saphyra.apphub.ci.menu.local_run_menu.edit_configuration_menu.local_run_thread_count_editer;

import com.github.saphyra.apphub.ci.value.DefaultProperties;
import com.github.saphyra.apphub.ci.value.LocalRunMode;
import com.github.saphyra.apphub.ci.dao.Property;
import com.github.saphyra.apphub.ci.dao.PropertyName;
import com.github.saphyra.apphub.ci.dao.PropertyRepository;
import com.github.saphyra.apphub.ci.utils.ValidatingInputReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
class LocalRunDefaulThreadCountEditerOption implements LocalRunThreadCountEditerMenuOption {
    private final PropertyRepository propertyRepository;
    private final DefaultProperties defaultProperties;
    private final ValidatingInputReader validatingInputReader;

    @Override
    public String getCommand() {
        return "1";
    }

    @Override
    public String getName() {
        return String.format("%s (%s)", LocalRunMode.DEFAULT, propertyRepository.findById(PropertyName.LOCAL_RUN_THREAD_COUNT_DEFAULT).map(Property::getValue).orElse(String.valueOf(defaultProperties.getLocalRunThreadCountDefault()))); //TODO translate
    }

    @Override
    public boolean process() {
        Integer threadCount = validatingInputReader.getInput(
            "Thread Count for running with tests:", //TODO translate
            Integer::parseInt,
            tc -> {
                if(tc < 1){
                    return Optional.of("Must not be lower than 1"); //TODO translate
                }

                return  Optional.empty();
            }
        );

        propertyRepository.save(new Property(PropertyName.LOCAL_RUN_THREAD_COUNT_DEFAULT, String.valueOf(threadCount)));

        log.info("Thread Count updated."); //TODO translate

        return false;
    }
}
