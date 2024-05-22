package com.github.saphyra.apphub.ci.menu.minikube;

import com.github.saphyra.apphub.ci.localization.LocalizationProvider;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.process.minikube.NamespaceNameProvider;
import com.github.saphyra.apphub.ci.process.minikube.local.MinikubeNamespaceDeletionProcess;
import com.github.saphyra.apphub.ci.utils.BooleanParser;
import com.github.saphyra.apphub.ci.utils.ValidatingInputReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
class MinikubeDeleteNamespaceMenuOption implements MinikubeMenuOption {
    private final NamespaceNameProvider namespaceNameProvider;
    private final ValidatingInputReader validatingInputReader;
    private final BooleanParser booleanParser;
    private final MinikubeNamespaceDeletionProcess namespaceDeletionProcess;

    @Override
    public String getCommand() {
        return "8";
    }

    @Override
    public LocalizationProvider getName() {
        return LocalizedText.DELETE_NAMESPACE;
    }

    @Override
    public boolean process() {
        String namespaceName = namespaceNameProvider.getBranchName();

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
