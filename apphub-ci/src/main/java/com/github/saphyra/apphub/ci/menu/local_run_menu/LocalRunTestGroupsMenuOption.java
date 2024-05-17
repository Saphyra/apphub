package com.github.saphyra.apphub.ci.menu.local_run_menu;

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
    public String getName() {
        return "Run Test Groups"; //TODO translate
    }

    @Override
    public boolean process() {
        String testGroups = validatingInputReader.getInput(
            "Which groups you would like to run?", s -> {
                if (s.isEmpty()) {
                    return Optional.of("Enter the test group");
                }

                return Optional.empty();
            }
        );

        localRunTestsProcess.run(testGroups);

        return false;
    }
}
