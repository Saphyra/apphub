package com.github.saphyra.apphub.ci.menu.production;

import com.github.saphyra.apphub.ci.localization.LocalizationProvider;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.process.minikube.production.ProductionStartProcess;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class ProductionStartMenuOption implements ProductionMenuOption {
    private final ProductionStartProcess productionStartProcess;

    @Override
    public String getCommand() {
        return "4";
    }

    @Override
    public LocalizationProvider getName() {
        return LocalizedText.MINIKUBE_START;
    }

    @Override
    public boolean process() {
        productionStartProcess.startServer();

        return false;
    }
}
