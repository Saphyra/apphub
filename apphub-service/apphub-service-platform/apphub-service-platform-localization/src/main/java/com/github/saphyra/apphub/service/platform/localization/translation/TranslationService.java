package com.github.saphyra.apphub.service.platform.localization.translation;

import com.github.saphyra.apphub.lib.common_domain.LocalizationKey;
import com.github.saphyra.apphub.lib.data.AbstractDataService;
import com.github.saphyra.apphub.lib.data.loader.ContentLoaderFactory;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.platform.localization.error_code.Localization;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Slf4j
public class TranslationService extends AbstractDataService<String, Localization> {
    private static final String TRANSLATION_NOT_FOUND_MESSAGE = "%s could not be translated.";

    private final ErrorReporterService errorReporterService;

    public TranslationService(ContentLoaderFactory contentLoaderFactory, ErrorReporterService errorReporterService) {
        super("translation", contentLoaderFactory);
        this.errorReporterService = errorReporterService;
    }

    public String translate(LocalizationKey key, String locale) {
        return getOptional(locale)
            .flatMap(localization -> localization.getOptional(key.name()))
            .orElseGet(() -> getDefault(locale, key));
    }

    private String getDefault(String locale, LocalizationKey key) {
        errorReporterService.report("Translation not found for key " + key + " and locale " + locale);
        return String.format(TRANSLATION_NOT_FOUND_MESSAGE, key);
    }

    @Override
    @PostConstruct
    public void init() {
        super.load(Localization.class);
        log.info("Translations loaded: {}", this);
    }

    @Override
    public void addItem(Localization content, String fileName) {
        put(FilenameUtils.removeExtension(fileName), content);
    }
}
