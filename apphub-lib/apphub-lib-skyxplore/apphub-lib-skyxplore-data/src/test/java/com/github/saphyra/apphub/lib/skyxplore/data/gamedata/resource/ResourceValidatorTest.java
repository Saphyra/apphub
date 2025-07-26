package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.ConstructionRequirements;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.ConstructionRequirementsValidator;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.GameDataItemValidator;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
    private ConstructionRequirementsValidator constructionRequirementsValidator;

    @Mock
    private GameDataItemValidator gameDataItemValidator;

    @InjectMocks
    private ResourceValidator underTest;

    @Mock
    private ResourceData resourceData;

    @Mock
    private ConstructionRequirements constructionRequirements;

    @BeforeEach
    void setUp(){
        given(resourceData.getConstructionRequirements()).willReturn(constructionRequirements);
    }

    @AfterEach
    void verify() {
        then(gameDataItemValidator).should().validate(resourceData);
        then(constructionRequirementsValidator).should().validate(constructionRequirements);
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