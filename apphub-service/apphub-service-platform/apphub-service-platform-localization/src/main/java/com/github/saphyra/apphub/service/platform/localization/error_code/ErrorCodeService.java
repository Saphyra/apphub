package com.github.saphyra.apphub.service.platform.localization.error_code;

import com.github.saphyra.apphub.lib.data.AbstractDataService;
import com.github.saphyra.apphub.lib.data.loader.ContentLoaderFactory;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;

@Component
public class ErrorCodeService extends AbstractDataService<String, ErrorCodeLocalization> {
    private static final String ERROR_CODE_NOT_FOUND_MESSAGE = "%s could not found.";

    public ErrorCodeService(ContentLoaderFactory contentLoaderFactory) {
        super("error_code", contentLoaderFactory);
    }

    @Override
    public void init() {
        super.load(ErrorCodeLocalization.class);
    }

    @Override
    public void addItem(ErrorCodeLocalization content, String fileName) {
        put(FilenameUtils.removeExtension(fileName), content);
    }

    public String getByLocaleAndErrorCode(String errorCode, String locale) {
        return getOptional(locale)
            .flatMap(errorCodeLocalization -> errorCodeLocalization.getOptional(errorCode))
            .orElseGet(() -> String.format(ERROR_CODE_NOT_FOUND_MESSAGE, errorCode));
    }
}
