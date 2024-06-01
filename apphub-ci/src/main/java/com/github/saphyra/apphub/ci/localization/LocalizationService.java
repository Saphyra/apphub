package com.github.saphyra.apphub.ci.localization;

import com.github.saphyra.apphub.ci.dao.PropertyDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LocalizationService {
    private final PropertyDao propertyDao;

    public void writeMessage(LocalizationProvider text) {
        log.info(getMessage(text));
    }

    public String getMessage(LocalizationProvider localizationProvider) {
        Language language = propertyDao.getLanguage();

        return localizationProvider.getLocalizedText(language);
    }
}
