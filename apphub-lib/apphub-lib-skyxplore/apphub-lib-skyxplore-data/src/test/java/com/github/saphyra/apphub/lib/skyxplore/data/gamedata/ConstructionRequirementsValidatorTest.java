package com.github.saphyra.apphub.lib.skyxplore.data.gamedata;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@ExtendWith(MockitoExtension.class)
public class ConstructionRequirementsValidatorTest {
    private static final Integer REQUIRED_WORK_POINTS = 32;
    private static final String REQUIRED_RESOURCE = "required-resource";

    @InjectMocks
    private ConstructionRequirementsValidator underTest;

    @Test
    public void nullRequiredWorkPoints() {
        ConstructionRequirements constructionRequirements = validConstructionRequirements()
            .toBuilder()
            .requiredWorkPoints(null)
            .build();

        assertThat(catchThrowable(() -> underTest.validate(constructionRequirements))).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void tooLowRequiredWorkPoints() {
        ConstructionRequirements constructionRequirements = validConstructionRequirements()
            .toBuilder()
            .requiredWorkPoints(0)
            .build();

        assertThat(catchThrowable(() -> underTest.validate(constructionRequirements))).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void nullRequiredResources() {
        ConstructionRequirements constructionRequirements = validConstructionRequirements()
            .toBuilder()
            .requiredResources(null)
            .build();

        assertThat(catchThrowable(() -> underTest.validate(constructionRequirements))).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void requiredResourcesContainsNull() {
        Map<String, Integer> requiredResources = new HashMap<>() {{
            put(REQUIRED_RESOURCE, null);
        }};
        ConstructionRequirements constructionRequirements = validConstructionRequirements()
            .toBuilder()
            .requiredResources(requiredResources)
            .build();

        assertThat(catchThrowable(() -> underTest.validate(constructionRequirements))).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void valid() {
        underTest.validate(validConstructionRequirements());
    }

    private ConstructionRequirements validConstructionRequirements() {
        HashMap<String, Integer> requiredResources = new HashMap<>() {{
            put(REQUIRED_RESOURCE, 0);
        }};
        return ConstructionRequirements.builder()
            .requiredWorkPoints(REQUIRED_WORK_POINTS)
            .requiredResources(requiredResources)
            .build();
    }
}