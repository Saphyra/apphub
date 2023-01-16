package com.github.saphyra.apphub.service.platform.localization.error_code;

import com.github.saphyra.apphub.lib.data.AbstractDataService;
import com.github.saphyra.apphub.lib.data.loader.ContentLoaderFactory;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import jakarta.annotation.PostConstruct;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;


@Component
public class ErrorCodeService extends AbstractDataService<String, Localization> {
    private static final String ERROR_CODE_NOT_FOUND_MESSAGE = "%s could not be translated.";

    private final ErrorReporterService errorReporterService;

    public ErrorCodeService(ContentLoaderFactory contentLoaderFactory, ErrorReporterService errorReporterService) {
        super("error_code", contentLoaderFactory);
        this.errorReporterService = errorReporterService;
    }

    @Override
    @PostConstruct
    public void init() {
        super.load(Localization.class);
    }

    @Override
    public void addItem(Localization content, String fileName) {
        put(FilenameUtils.removeExtension(fileName), content);
    }

    public String getByLocaleAndErrorCode(String errorCode, String locale) {
        return getOptional(locale)
            .flatMap(localization -> localization.getOptional(errorCode))
            .orElseGet(() -> getDefault(locale, errorCode));
    }

    private String getDefault(String locale, String errorCode) {
        errorReporterService.report("ErrorCode localization not found for errorCode " + errorCode + " and locale " + locale);
        return String.format(ERROR_CODE_NOT_FOUND_MESSAGE, errorCode);
    }
}
