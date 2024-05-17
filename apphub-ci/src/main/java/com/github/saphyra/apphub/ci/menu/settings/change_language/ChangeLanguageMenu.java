package com.github.saphyra.apphub.ci.menu.settings.change_language;

import com.github.saphyra.apphub.ci.dao.PropertyDao;
import com.github.saphyra.apphub.ci.localization.Language;
import com.github.saphyra.apphub.ci.localization.LocalizationService;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.MenuBase;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ChangeLanguageMenu extends MenuBase<ChangeLanguageMenuOption> {
    ChangeLanguageMenu(PropertyDao propertyDao, LocalizationService localizationService) {
        super(getSupportedLanguages(propertyDao), localizationService);
    }

    @Override
    protected LocalizedText getName() {
        return LocalizedText.SELECT_LANGUAGE;
    }

    private static List<ChangeLanguageMenuOption> getSupportedLanguages(PropertyDao propertyDao) {
        Language[] languages = Language.values();

        List<ChangeLanguageMenuOption> result = new ArrayList<>();
        for (int i = 0; i < languages.length; i++) {
            ChangeLanguageMenuOption option = ChangeLanguageMenuOption.builder()
                .command(String.valueOf(i + 1))
                .language(languages[i])
                .propertyDao(propertyDao)
                .build();

            result.add(option);
        }

        return result;
    }
}
