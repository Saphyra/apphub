package com.github.saphyra.apphub.ci.menu.production;

import com.github.saphyra.apphub.ci.localization.LocalizationProvider;
import com.github.saphyra.apphub.ci.localization.LocalizationService;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.MenuBase;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductionMenu extends MenuBase<ProductionMenuOption> {
    public ProductionMenu(List<ProductionMenuOption> productionMenuOptions, LocalizationService localizationService) {
        super(productionMenuOptions, localizationService);
    }

    @Override
    protected LocalizationProvider getName() {
        return LocalizedText.PRODUCTION;
    }
}
