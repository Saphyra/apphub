package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.ConstructionRequirements;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.ConstructionRequirementsValidator;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceData;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceDataService;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ProductionValidatorTest {
    private static final String RESOURCE_DATA_ID = "resource-data-id";

    @Mock
    private ConstructionRequirementsValidator constructionRequirementsValidator;

    @Spy
    private final ResourceDataService resourceDataService = new ResourceDataService();

    @InjectMocks
    private ProductionValidator underTest;

    @Mock
    private Production production;

    @Mock
    private ConstructionRequirements constructionRequirements;

    @Mock
    private ResourceData resourceData;

    @Test
    void nullProduces() {
        ExceptionValidator.validateInvalidParam(() -> underTest.validate(Collections.emptyList()), "produces", "must not be empty");
    }

    @Test
    void amountTooLow() {
        given(production.getConstructionRequirements()).willReturn(constructionRequirements);
        given(production.getAmount()).willReturn(0);

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(List.of(production)), "amount", "too low");

        then(constructionRequirementsValidator).should().validate(constructionRequirements);
    }

    @Test
    void blankResourceDataId() {
        given(production.getConstructionRequirements()).willReturn(constructionRequirements);
        given(production.getAmount()).willReturn(1);
        given(production.getResourceDataId()).willReturn(" ");

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(List.of(production)), "resourceDataId", "must not be null or blank");

        then(constructionRequirementsValidator).should().validate(constructionRequirements);
    }

    @Test
    void resourceDataIdDoesNotExist() {
        given(production.getConstructionRequirements()).willReturn(constructionRequirements);
        given(production.getAmount()).willReturn(1);
        given(production.getResourceDataId()).willReturn(RESOURCE_DATA_ID);

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(List.of(production)), "resourceDataId=%s".formatted(RESOURCE_DATA_ID), "invalid value");

        then(constructionRequirementsValidator).should().validate(constructionRequirements);
    }

    @Test
    void nullRequiredSkill() {
        given(production.getConstructionRequirements()).willReturn(constructionRequirements);
        given(production.getAmount()).willReturn(1);
        given(production.getResourceDataId()).willReturn(RESOURCE_DATA_ID);
        resourceDataService.put(RESOURCE_DATA_ID, resourceData);

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(List.of(production)), "requiredSkill", "must not be null");

        then(constructionRequirementsValidator).should().validate(constructionRequirements);
    }

    @Test
    void valid() {
        given(production.getConstructionRequirements()).willReturn(constructionRequirements);
        given(production.getAmount()).willReturn(1);
        given(production.getResourceDataId()).willReturn(RESOURCE_DATA_ID);
        resourceDataService.put(RESOURCE_DATA_ID, resourceData);
        given(production.getRequiredSkill()).willReturn(SkillType.AIMING);

        underTest.validate(List.of(production));

        then(constructionRequirementsValidator).should().validate(constructionRequirements);
    }
}