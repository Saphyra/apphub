package com.github.saphyra.apphub.ci.menu.local_run_menu;

import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.process.local.run_tests.LocalRunTestsProcess;
import com.github.saphyra.apphub.ci.utils.ValidatingInputReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
class LocalRunTestGroupsMenuOption implements LocalRunMenuOption {
    private final ValidatingInputReader validatingInputReader;
    private final LocalRunTestsProcess localRunTestsProcess;

    @Override
    public String getCommand() {
        return "5";
    }

    @Override
    public LocalizedText getName() {
        return LocalizedText.LOCAL_RUN_TEST_GROUPS;
    }

    @Override
    public boolean process() {
        String testGroups = validatingInputReader.getInput(
            LocalizedText.WHICH_TEST_GROUPS_TO_RUN,
            s -> {
                if (s.isEmpty()) {
                    return Optional.of(LocalizedText.ENTER_TEST_GROUP);
                }

                return Optional.empty();
            }
        );

        localRunTestsProcess.run(testGroups);

        return false;
    }
}
