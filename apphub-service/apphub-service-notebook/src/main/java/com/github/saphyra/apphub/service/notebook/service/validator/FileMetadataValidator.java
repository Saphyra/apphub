package com.github.saphyra.apphub.service.notebook.service.validator;

import com.github.saphyra.apphub.api.notebook.model.request.FileMetadata;
import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class FileMetadataValidator {
    private final CommonConfigProperties commonConfigProperties;

    public void validate(FileMetadata metadata) {
        ValidationUtil.notNull(metadata, "metadata");

        if (isNull(metadata.getStoredFileId())) {
            ValidationUtil.notBlank(metadata.getFileName(), "fileName");
            ValidationUtil.maximum(metadata.getSize(), commonConfigProperties.getMaxUploadedFileSize(), "fileName");
        }
    }
}
