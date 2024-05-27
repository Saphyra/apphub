package com.github.saphyra.apphub.ci.menu.production.edit_configuration;

import com.github.saphyra.apphub.ci.localization.LocalizationProvider;
import com.github.saphyra.apphub.ci.localization.LocalizationService;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.MenuBase;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductionEditConfigurationsMenu extends MenuBase<ProductionEditConfigurationsMenuOption> {
    ProductionEditConfigurationsMenu(List<ProductionEditConfigurationsMenuOption> productionEditConfigurationsMenuOptions, LocalizationService localizationService) {
        super(productionEditConfigurationsMenuOptions, localizationService);
    }

    @Override
    protected LocalizationProvider getName() {
        return LocalizedText.EDIT_CONFIGURATION;
    }
}
