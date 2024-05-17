package com.github.saphyra.apphub.ci.menu.settings.change_language;

import com.github.saphyra.apphub.ci.dao.PropertyDao;
import com.github.saphyra.apphub.ci.dao.PropertyName;
import com.github.saphyra.apphub.ci.localization.Language;
import com.github.saphyra.apphub.ci.localization.LocalizationProvider;
import com.github.saphyra.apphub.ci.localization.LocalizationService;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.MenuOption;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Builder
class ChangeLanguageMenuOption implements MenuOption {
    private final String command;
    private final Language language;
    private final PropertyDao propertyDao;
    private final LocalizationService localizationService;

    @Override
    public String getCommand() {
        return command;
    }

    @Override
    public LocalizationProvider getName() {
        return currentLanguage -> language.getLocalization().getLocalizedText(currentLanguage);
    }

    @Override
    public boolean process() {
        propertyDao.save(PropertyName.LANGUAGE, language.name());

        localizationService.writeMessage(LocalizedText.LANGUAGE_CHANGED);

        return true;
    }
}
