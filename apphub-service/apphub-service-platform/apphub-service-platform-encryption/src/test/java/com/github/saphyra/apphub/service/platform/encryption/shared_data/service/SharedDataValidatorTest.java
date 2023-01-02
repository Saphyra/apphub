package com.github.saphyra.apphub.service.platform.encryption.shared_data.service;

import com.github.saphyra.apphub.api.platform.encryption.model.AccessMode;
import com.github.saphyra.apphub.api.platform.encryption.model.DataType;
import com.github.saphyra.apphub.api.platform.encryption.model.SharedData;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;

public class SharedDataValidatorTest {
    private final SharedDataValidator underTest = new SharedDataValidator();

    @Test
    public void creation_valid() {
        SharedData sharedData = createValid()
            .build();

        underTest.validateForCreation(sharedData);
    }

    @Test
    public void creation_nullExternalId() {
        SharedData sharedData = createValid()
            .externalId(null)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validateForCreation(sharedData));

        ExceptionValidator.validateInvalidParam(ex, "externalId", "must not be null");
    }

    @Test
    public void creation_nullDataType() {
        SharedData sharedData = createValid()
            .dataType(null)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validateForCreation(sharedData));

        ExceptionValidator.validateInvalidParam(ex, "dataType", "must not be null");
    }

    @Test
    public void creation_nullSharedWithAndPublicData() {
        SharedData sharedData = createValid()
            .sharedWith(null)
            .publicData(null)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validateForCreation(sharedData));

        ExceptionValidator.validateInvalidParam(ex, "sharedWith, publicData", "all values are null");
    }

    @Test
    public void creation_nullAccessMode() {
        SharedData sharedData = createValid()
            .accessMode(null)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validateForCreation(sharedData));

        ExceptionValidator.validateInvalidParam(ex, "accessMode", "must not be null");
    }

    @Test
    public void cloning_valid() {
        SharedData sharedData = createValid()
            .build();

        underTest.validateForCloning(sharedData);
    }

    @Test
    public void cloning_nullExternalId() {
        SharedData sharedData = createValid()
            .externalId(null)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validateForCloning(sharedData));

        ExceptionValidator.validateInvalidParam(ex, "externalId", "must not be null");
    }

    @Test
    public void cloning_nullDataType() {
        SharedData sharedData = createValid()
            .dataType(null)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validateForCloning(sharedData));

        ExceptionValidator.validateInvalidParam(ex, "dataType", "must not be null");
    }

    private SharedData.SharedDataBuilder createValid() {
        return SharedData.builder()
            .externalId(UUID.randomUUID())
            .dataType(DataType.TEST)
            .sharedWith(UUID.randomUUID())
            .accessMode(AccessMode.EDIT);
    }
}