package com.github.saphyra.apphub.lib.skyxplore.data.gamedata;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceDataService;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ConstructionRequirementResourceValidatorTest {
    private static final String RESOURCE_DATA_ID = "resource-data-id";
    @Mock
    private ConstructionRequirementsProvider constructionRequirementsProvider;

    @Mock
    private ResourceDataService resourceDataService;

    private ConstructionRequirementResourceValidator underTest;

    @Mock
    private ConstructionRequirements constructionRequirements;

    @BeforeEach
    void setUp(){
        underTest = ConstructionRequirementResourceValidator.builder()
            .constructionRequirementsProviders(List.of(constructionRequirementsProvider))
            .resourceDataService(resourceDataService)
            .build();
    }

    @Test
    void resourceDoesNotExist(){
        given(constructionRequirementsProvider.getConstructionRequirements()).willReturn(List.of(constructionRequirements));
        given(constructionRequirements.getRequiredResources()).willReturn(Map.of(RESOURCE_DATA_ID, 1));
        given(resourceDataService.containsKey(RESOURCE_DATA_ID)).willReturn(false);

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(), "requiredResource=resource-data-id", "invalid value");
    }

    @Test
    void resourceExists(){
        given(constructionRequirementsProvider.getConstructionRequirements()).willReturn(List.of(constructionRequirements));
        given(constructionRequirements.getRequiredResources()).willReturn(Map.of(RESOURCE_DATA_ID, 1));
        given(resourceDataService.containsKey(RESOURCE_DATA_ID)).willReturn(true);

        underTest.validate();
    }
}