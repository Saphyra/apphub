package com.github.saphyra.apphub.ci.menu.production;

import com.github.saphyra.apphub.ci.localization.LocalizationProvider;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.process.minikube.production.ProductionReleaseProcess;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class ProductionReleaseMenuOption implements ProductionMenuOption {
    private final ProductionReleaseProcess productionReleaseProcess;

    @Override
    public String getCommand() {
        return "1";
    }

    @Override
    public LocalizationProvider getName() {
        return LocalizedText.PRODUCTION_RELEASE;
    }

    @Override
    public boolean process() {
        productionReleaseProcess.release();

        return false;
    }
}
