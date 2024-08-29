package com.github.saphyra.apphub.ci.menu.minikube;

import com.github.saphyra.apphub.ci.localization.LocalizationProvider;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.Menu;
import com.github.saphyra.apphub.ci.menu.MenuOption;
import com.github.saphyra.apphub.ci.process.minikube.NamespaceNameProvider;
import com.github.saphyra.apphub.ci.process.minikube.local.MinikubeNamespaceDeletionProcess;
import com.github.saphyra.apphub.ci.utils.BooleanParser;
import com.github.saphyra.apphub.ci.utils.ValidatingInputReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
class MinikubeDeleteNamespaceMenuOption implements MenuOption {
    private final NamespaceNameProvider namespaceNameProvider;
    private final ValidatingInputReader validatingInputReader;
    private final BooleanParser booleanParser;
    private final MinikubeNamespaceDeletionProcess namespaceDeletionProcess;

    @Override
    public Menu getMenu() {
        return Menu.MINIKUBE_MENU;
    }

    @Override
    public String getCommand() {
        return "6";
    }

    @Override
    public LocalizationProvider getName() {
        return LocalizedText.DELETE_NAMESPACE;
    }

    @Override
    public boolean process() {
        String namespaceName = namespaceNameProvider.getNamespaceName();

        boolean result = validatingInputReader.getInput(
            language -> LocalizedText.CONFIRM_DELETE_NAMESPACE.getLocalizedText(language).formatted(namespaceName),
            booleanParser::parse,
            aBoolean -> Optional.empty()
        );

        if(result){
            namespaceDeletionProcess.deleteNamespace(namespaceName);
        }

        return false;
    }
}
