package com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.service;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.CreateToolRequest;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.StorageBoxModel;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.ToolTypeModel;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class CreateToolRequestValidatorTest {
    private static final StorageBoxModel STORAGE_BOX = new StorageBoxModel();
    private static final ToolTypeModel TOOL_TYPE = new ToolTypeModel();

    @Mock
    private ToolTypeValidator toolTypeValidator;

    @Mock
    private StorageBoxValidator storageBoxValidator;

    @InjectMocks
    private CreateToolRequestValidator underTest;

    @Test
    void nullStorageBox() {
        CreateToolRequest request = CreateToolRequest.builder()
            .storageBox(null)
            .toolType(TOOL_TYPE)
            .brand("asd")
            .name("asd")
            .cost(32)
            .acquiredAt(LocalDate.now())
            .build();

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "storageBox", "must not be null");
    }

    @Test
    void nullToolType() {
        CreateToolRequest request = CreateToolRequest.builder()
            .storageBox(STORAGE_BOX)
            .toolType(null)
            .brand("asd")
            .name("asd")
            .cost(32)
            .acquiredAt(LocalDate.now())
            .build();

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "toolType", "must not be null");
    }

    @Test
    void nullBrand() {
        CreateToolRequest request = CreateToolRequest.builder()
            .storageBox(STORAGE_BOX)
            .toolType(TOOL_TYPE)
            .brand(null)
            .name("asd")
            .cost(32)
            .acquiredAt(LocalDate.now())
            .build();

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "brand", "must not be null");
    }

    @Test
    void blankName() {
        CreateToolRequest request = CreateToolRequest.builder()
            .storageBox(STORAGE_BOX)
            .toolType(TOOL_TYPE)
            .brand("asd")
            .name(" ")
            .cost(32)
            .acquiredAt(LocalDate.now())
            .build();

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "name", "must not be null or blank");
    }

    @Test
    void nullCost() {
        CreateToolRequest request = CreateToolRequest.builder()
            .storageBox(STORAGE_BOX)
            .toolType(TOOL_TYPE)
            .brand("asd")
            .name("asd")
            .cost(null)
            .acquiredAt(LocalDate.now())
            .build();

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "cost", "must not be null");
    }

    @Test
    void nullAcquiredAt() {
        CreateToolRequest request = CreateToolRequest.builder()
            .storageBox(STORAGE_BOX)
            .toolType(TOOL_TYPE)
            .brand("asd")
            .name("asd")
            .cost(32)
            .acquiredAt(null)
            .build();

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "acquiredAt", "must not be null");
    }

    @Test
    void valid() {
        CreateToolRequest request = CreateToolRequest.builder()
            .storageBox(STORAGE_BOX)
            .toolType(TOOL_TYPE)
            .brand("asd")
            .name("asd")
            .cost(32)
            .acquiredAt(LocalDate.now())
            .build();

        underTest.validate(request);

        then(toolTypeValidator).should().validate(TOOL_TYPE);
        then(storageBoxValidator).should().validate(STORAGE_BOX);
    }
}