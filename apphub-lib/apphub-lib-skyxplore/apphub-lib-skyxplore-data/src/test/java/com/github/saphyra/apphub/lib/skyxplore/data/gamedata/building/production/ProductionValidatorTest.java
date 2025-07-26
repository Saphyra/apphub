package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production;

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

@ExtendWith(MockitoExtension.class)
class ProductionValidatorTest {
    private static final String RESOURCE_DATA_ID = "resource-data-id";

    @Spy
    private final ResourceDataService resourceDataService = new ResourceDataService();

    @InjectMocks
    private ProductionValidator underTest;

    @Mock
    private Production production;

    @Mock
    private ResourceData resourceData;

    @Test
    void nullProduces() {
        ExceptionValidator.validateInvalidParam(() -> underTest.validate(Collections.emptyList()), "produces", "must not be empty");
    }

    @Test
    void amountTooLow() {
        given(production.getAmount()).willReturn(0);

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(List.of(production)), "amount", "too low");
    }

    @Test
    void blankResourceDataId() {
        given(production.getAmount()).willReturn(1);
        given(production.getResourceDataId()).willReturn(" ");

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(List.of(production)), "resourceDataId", "must not be null or blank");
    }

    @Test
    void resourceDataIdDoesNotExist() {
        given(production.getAmount()).willReturn(1);
        given(production.getResourceDataId()).willReturn(RESOURCE_DATA_ID);

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(List.of(production)), "resourceDataId=%s".formatted(RESOURCE_DATA_ID), "invalid value");
    }

    @Test
    void nullRequiredSkill() {
        given(production.getAmount()).willReturn(1);
        given(production.getResourceDataId()).willReturn(RESOURCE_DATA_ID);
        resourceDataService.put(RESOURCE_DATA_ID, resourceData);

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(List.of(production)), "requiredSkill", "must not be null");
    }

    @Test
    void valid() {
        given(production.getAmount()).willReturn(1);
        given(production.getResourceDataId()).willReturn(RESOURCE_DATA_ID);
        resourceDataService.put(RESOURCE_DATA_ID, resourceData);
        given(production.getRequiredSkill()).willReturn(SkillType.AIMING);

        underTest.validate(List.of(production));
    }
}