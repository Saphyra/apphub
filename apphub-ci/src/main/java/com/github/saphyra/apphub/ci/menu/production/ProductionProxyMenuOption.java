package com.github.saphyra.apphub.ci.menu.production;

import com.github.saphyra.apphub.ci.localization.LocalizationProvider;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.process.minikube.production.StartProductionProxyProcess;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
 class ProductionProxyMenuOption implements ProductionMenuOption{
    private final StartProductionProxyProcess startProductionProxyProcess;

    @Override
    public String getCommand() {
        return "2";
    }

    @Override
    public LocalizationProvider getName() {
        return LocalizedText.PRODUCTION_PROXY;
    }

    @Override
    public boolean process() {
        startProductionProxyProcess.startProductionProxy();

        return false;
    }
}
