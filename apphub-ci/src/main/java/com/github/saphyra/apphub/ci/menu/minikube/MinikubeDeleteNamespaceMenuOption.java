package com.github.saphyra.apphub.ci.menu.minikube;

import com.github.saphyra.apphub.ci.localization.LocalizationProvider;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.Menu;
import com.github.saphyra.apphub.ci.menu.MenuOption;
import com.github.saphyra.apphub.ci.menu.MenuOrder;
import com.github.saphyra.apphub.ci.menu.MenuOrderEnum;
import com.github.saphyra.apphub.ci.process.minikube.NamespaceNameProvider;
import com.github.saphyra.apphub.ci.process.minikube.local.MinikubeNamespaceDeletionProcess;
import com.github.saphyra.apphub.ci.utils.BooleanParser;
import com.github.saphyra.apphub.ci.utils.ValidatingInputReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static java.util.Objects.isNull;

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
    public MenuOrder getOrder() {
        return MenuOrderEnum.DELETE_NAMESPACE;
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
            aBoolean -> isNull(aBoolean) ? Optional.of(LocalizedText.PROVIDE_YES_OR_NO) : Optional.empty()
        );

        if(result){
            namespaceDeletionProcess.deleteNamespace(namespaceName);
        }

        return false;
    }
}
