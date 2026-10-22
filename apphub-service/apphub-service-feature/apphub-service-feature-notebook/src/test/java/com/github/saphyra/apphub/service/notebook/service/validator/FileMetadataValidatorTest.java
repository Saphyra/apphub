package com.github.saphyra.apphub.service.notebook.service.validator;

import com.github.saphyra.apphub.api.feature.notebook.model.request.FileMetadata;
import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class FileMetadataValidatorTest {
    private static final Long MAX_FILE_SIZE = 2L;

    @Mock
    private CommonConfigProperties commonConfigProperties;

    @InjectMocks
    private FileMetadataValidator underTest;

    @Test
    void nullMetadata() {
        Throwable ex = catchThrowable(() -> underTest.validate(null));

        ExceptionValidator.validateInvalidParam(ex, "metadata", ValidationUtil.MUST_NOT_BE_NULL);
    }

    @Test
    void storedFileIdFilled() {
        FileMetadata metadata = FileMetadata.builder()
            .storedFileId(UUID.randomUUID())
            .build();

        underTest.validate(metadata);
    }

    @Test
    void nullFilename() {
        FileMetadata metadata = FileMetadata.builder()
            .fileName(null)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(metadata));

        ExceptionValidator.validateInvalidParam(ex, "fileName", ValidationUtil.MUST_NOT_BE_NULL_OR_BLANK);
    }

    @Test
    void blankFilename() {
        FileMetadata metadata = FileMetadata.builder()
            .fileName(" ")
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(metadata));

        ExceptionValidator.validateInvalidParam(ex, "fileName", ValidationUtil.MUST_NOT_BE_NULL_OR_BLANK);
    }

    @Test
    void nullFileSize() {
        FileMetadata metadata = FileMetadata.builder()
            .fileName("asd")
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(metadata));

        ExceptionValidator.validateInvalidParam(ex, "fileSize", ValidationUtil.MUST_NOT_BE_NULL);
    }

    @Test
    void tooLargeFileSize() {
        given(commonConfigProperties.getMaxUploadedFileSize()).willReturn(MAX_FILE_SIZE);

        FileMetadata metadata = FileMetadata.builder()
            .fileName("asd")
            .size(MAX_FILE_SIZE + 1)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(metadata));

        ExceptionValidator.validateInvalidParam(ex, "fileSize", ValidationUtil.TOO_HIGH);
    }

    @Test
    void valid() {
        given(commonConfigProperties.getMaxUploadedFileSize()).willReturn(MAX_FILE_SIZE);

        FileMetadata metadata = FileMetadata.builder()
            .fileName("asd")
            .size(MAX_FILE_SIZE)
            .build();

        underTest.validate(metadata);
    }
}