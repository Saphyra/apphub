package com.github.saphyra.apphub.service.platform.web_content.error_code;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.data.AbstractDataService;
import com.github.saphyra.apphub.lib.data.loader.ContentLoaderFactory;
import jakarta.annotation.PostConstruct;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;

@Component
public class ErrorCodeService extends AbstractDataService<String, Localization> {
    private static final String ERROR_CODE_NOT_FOUND_MESSAGE = "%s could not be translated.";

    public ErrorCodeService(ContentLoaderFactory contentLoaderFactory) {
        super("error_code", contentLoaderFactory);
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

    public String getByLocaleAndErrorCode(ErrorCode errorCode, String locale) {
        return getByLocaleAndErrorCode(errorCode.name(), locale);
    }

    public String getByLocaleAndErrorCode(String errorCode, String locale) {
        return getOptional(locale)
            .flatMap(localization -> localization.getOptional(errorCode))
            .orElseGet(() -> getDefault(errorCode));
    }

    private String getDefault(String errorCode) {
        return String.format(ERROR_CODE_NOT_FOUND_MESSAGE, errorCode);
    }
}
