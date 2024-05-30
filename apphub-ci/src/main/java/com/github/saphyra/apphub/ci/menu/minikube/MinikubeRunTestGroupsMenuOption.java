package com.github.saphyra.apphub.ci.menu.minikube;

import com.github.saphyra.apphub.ci.localization.LocalizationProvider;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.Menu;
import com.github.saphyra.apphub.ci.menu.MenuOption;
import com.github.saphyra.apphub.ci.process.minikube.local.MinikubeLocalRunTestsProcess;
import com.github.saphyra.apphub.ci.utils.ValidatingInputReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
class MinikubeRunTestGroupsMenuOption implements MenuOption {
    private final MinikubeLocalRunTestsProcess minikubeLocalRunTestsProcess;
    private final ValidatingInputReader validatingInputReader;

    @Override
    public Menu getMenu() {
        return Menu.MINIKUBE_MENU;
    }

    @Override
    public String getCommand() {
        return "5";
    }

    @Override
    public LocalizationProvider getName() {
        return LocalizedText.RUN_TEST_GROUPS;
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

        minikubeLocalRunTestsProcess.runTests(testGroups);

        return false;
    }
}
