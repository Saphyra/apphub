package com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.service;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.CreateToolRequest;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

@ExtendWith(MockitoExtension.class)
class CreateToolRequestValidatorTest {
    private final CreateToolRequestValidator underTest = new CreateToolRequestValidator();

    @Test
    void nullBrand() {
        CreateToolRequest request = CreateToolRequest.builder()
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
            .brand("asd")
            .name("asd")
            .cost(32)
            .acquiredAt(LocalDate.now())
            .build();

        underTest.validate(request);
    }
}