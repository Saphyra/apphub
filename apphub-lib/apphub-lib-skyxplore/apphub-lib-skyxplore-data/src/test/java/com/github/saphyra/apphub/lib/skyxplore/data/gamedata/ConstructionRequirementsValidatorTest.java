package com.github.saphyra.apphub.lib.skyxplore.data.gamedata;

import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ConstructionRequirementsValidatorTest {
    private static final String RESOURCE_DATA_ID = "resource-data-id";

    @InjectMocks
    private ConstructionRequirementsValidator underTest;

    @Mock
    private ConstructionRequirements constructionRequirements;

    @Test
    void nullConstructionRequirements() {
        ExceptionValidator.validateInvalidParam(() -> underTest.validate(null), "constructionRequirements", ValidationUtil.MUST_NOT_BE_NULL);
    }

    @Test
    void tooLowRequiredWorkPoints() {
        given(constructionRequirements.getRequiredWorkPoints()).willReturn(-1);

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(constructionRequirements), "requiredWorkPoints", ValidationUtil.TOO_LOW);
    }

    @Test
    void tooLowRequiredEnergy() {
        given(constructionRequirements.getRequiredWorkPoints()).willReturn(0);
        given(constructionRequirements.getRequiredEnergy()).willReturn(-1);

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(constructionRequirements), "requiredEnergy", ValidationUtil.TOO_LOW);
    }

    @Test
    void nullInRequiredResources() {
        given(constructionRequirements.getRequiredWorkPoints()).willReturn(0);
        given(constructionRequirements.getRequiredEnergy()).willReturn(0);
        given(constructionRequirements.getRequiredResources()).willReturn(CollectionUtils.singleValueMap(RESOURCE_DATA_ID, null));

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(constructionRequirements), "requiredResources.%s".formatted(RESOURCE_DATA_ID), ValidationUtil.MUST_NOT_BE_NULL);
    }

    @Test
    void valid() {
        given(constructionRequirements.getRequiredWorkPoints()).willReturn(0);
        given(constructionRequirements.getRequiredEnergy()).willReturn(0);
        given(constructionRequirements.getRequiredResources()).willReturn(CollectionUtils.singleValueMap(RESOURCE_DATA_ID, 3));

        underTest.validate(constructionRequirements);
    }
}