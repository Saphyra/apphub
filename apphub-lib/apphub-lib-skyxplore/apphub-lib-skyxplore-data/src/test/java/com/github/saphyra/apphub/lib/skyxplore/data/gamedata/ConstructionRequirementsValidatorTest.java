package com.github.saphyra.apphub.lib.skyxplore.data.gamedata;

import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceData;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceDataService;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ConstructionRequirementsValidatorTest {
    private static final String RESOURCE_DATA_ID = "resource-data-id";

    @Spy
    private final ResourceDataService resourceDataService = new ResourceDataService();

    @InjectMocks
    private ConstructionRequirementsValidator underTest;

    @Mock
    private ConstructionRequirements constructionRequirements;

    @Mock
    private ResourceData resourceData;

    @Test
    void nullConstructionRequirements() {
        ExceptionValidator.validateInvalidParam(() -> underTest.validate(null), "constructionRequirements", "must not be null");
    }

    @Test
    void tooLowRequiredWorkPoints() {
        given(constructionRequirements.getRequiredWorkPoints()).willReturn(-1);

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(constructionRequirements), "requiredWorkPoints", "too low");
    }

    @Test
    void tooLowRequiredEnergy() {
        given(constructionRequirements.getRequiredWorkPoints()).willReturn(0);
        given(constructionRequirements.getRequiredEnergy()).willReturn(-1);

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(constructionRequirements), "requiredEnergy", "too low");
    }

    @Test
    void nullInRequiredResources() {
        given(constructionRequirements.getRequiredWorkPoints()).willReturn(0);
        given(constructionRequirements.getRequiredEnergy()).willReturn(0);
        given(constructionRequirements.getRequiredResources()).willReturn(CollectionUtils.singleValueMap(RESOURCE_DATA_ID, null));

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(constructionRequirements), "requiredResources.%s".formatted(RESOURCE_DATA_ID), "must not be null");
    }

    @Test
    void resourceDataIdDoesNotExist() {
        given(constructionRequirements.getRequiredWorkPoints()).willReturn(0);
        given(constructionRequirements.getRequiredEnergy()).willReturn(0);
        given(constructionRequirements.getRequiredResources()).willReturn(CollectionUtils.singleValueMap(RESOURCE_DATA_ID, 3));

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(constructionRequirements), "requiredResource=%s".formatted(RESOURCE_DATA_ID), "invalid value");
    }

    @Test
    void valid() {
        given(constructionRequirements.getRequiredWorkPoints()).willReturn(0);
        given(constructionRequirements.getRequiredEnergy()).willReturn(0);
        given(constructionRequirements.getRequiredResources()).willReturn(CollectionUtils.singleValueMap(RESOURCE_DATA_ID, 3));

        resourceDataService.put(RESOURCE_DATA_ID, resourceData);

        underTest.validate(constructionRequirements);
    }
}