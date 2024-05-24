package com.github.saphyra.apphub.ci.menu.production;

import com.github.saphyra.apphub.ci.localization.LocalizationProvider;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.process.minikube.production.ProductionRunTestsProcess;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class ProductionRunTestsMenuOption implements ProductionMenuOption {
    private final ProductionRunTestsProcess productionRunTestsProcess;

    @Override
    public String getCommand() {
        return "5";
    }

    @Override
    public LocalizationProvider getName() {
        return LocalizedText.RUN_TESTS;
    }

    @Override
    public boolean process() {
        productionRunTestsProcess.runTests();

        return false;
    }
}
