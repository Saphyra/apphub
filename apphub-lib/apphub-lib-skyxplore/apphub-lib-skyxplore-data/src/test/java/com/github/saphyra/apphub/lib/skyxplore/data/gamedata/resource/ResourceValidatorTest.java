package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.GameDataItemValidator;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ResourceValidatorTest {
    private static final String KEY = "key";

    @Mock
    private GameDataItemValidator gameDataItemValidator;

    @InjectMocks
    private ResourceValidator underTest;

    @Mock
    private ResourceData resourceData;

    @AfterEach
    void setUp() {
        then(gameDataItemValidator).should().validate(resourceData);
    }

    @Test
    void nullStorageType() {
        ExceptionValidator.validateInvalidParam(() -> underTest.validate(Map.of(KEY, resourceData)), "storageType", "must not be null");
    }

    @Test
    void valid() {
        given(resourceData.getStorageType()).willReturn(StorageType.AMMUNITION);

        underTest.validate(Map.of(KEY, resourceData));
    }
}