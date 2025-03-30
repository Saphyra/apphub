package com.github.saphyra.apphub.ci.menu.preprod;

import com.github.saphyra.apphub.ci.localization.LocalizationProvider;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.Menu;
import com.github.saphyra.apphub.ci.menu.MenuOption;
import com.github.saphyra.apphub.ci.menu.MenuOrderEnum;
import com.github.saphyra.apphub.ci.process.minikube.preprod.PreprodRunTestsProcess;
import com.github.saphyra.apphub.ci.utils.ValidatingInputReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
class PreprodRunTestGroupsMenuOption implements MenuOption {
    private final PreprodRunTestsProcess preprodRunTestsProcess;
    private final ValidatingInputReader validatingInputReader;

    @Override
    public Menu getMenu() {
        return Menu.PREPROD_MENU;
    }

    @Override
    public MenuOrderEnum getOrder() {
        return MenuOrderEnum.RUN_TEST_GROUPS;
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

        preprodRunTestsProcess.runTests(testGroups);

        return false;
    }
}
