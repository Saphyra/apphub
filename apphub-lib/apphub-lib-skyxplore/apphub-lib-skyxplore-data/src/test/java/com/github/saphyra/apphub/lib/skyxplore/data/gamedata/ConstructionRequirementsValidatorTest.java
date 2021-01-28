package com.github.saphyra.apphub.lib.skyxplore.data.gamedata;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class ConstructionRequirementsValidatorTest {
    private static final Integer REQUIRED_WORK_POINTS = 32;
    private static final String REQUIRED_RESOURCE = "required-resource";

    @InjectMocks
    private ConstructionRequirementsValidator underTest;

    @Test(expected = NullPointerException.class)
    public void nullRequiredWorkPoints() {
        ConstructionRequirements constructionRequirements = validConstructionRequirements()
            .toBuilder()
            .requiredWorkPoints(null)
            .build();

        underTest.validate(constructionRequirements);
    }

    @Test(expected = IllegalStateException.class)
    public void tooLowRequiredWorkPoints() {
        ConstructionRequirements constructionRequirements = validConstructionRequirements()
            .toBuilder()
            .requiredWorkPoints(0)
            .build();

        underTest.validate(constructionRequirements);
    }

    @Test(expected = NullPointerException.class)
    public void nullRequiredResources() {
        ConstructionRequirements constructionRequirements = validConstructionRequirements()
            .toBuilder()
            .requiredResources(null)
            .build();

        underTest.validate(constructionRequirements);
    }

    @Test(expected = NullPointerException.class)
    public void requiredResourcesContainsNull() {
        Map<String, Integer> requiredResources = new HashMap<String, Integer>() {{
            put(REQUIRED_RESOURCE, null);
        }};
        ConstructionRequirements constructionRequirements = validConstructionRequirements()
            .toBuilder()
            .requiredResources(requiredResources)
            .build();

        underTest.validate(constructionRequirements);
    }

    @Test
    public void valid() {
        underTest.validate(validConstructionRequirements());
    }

    private ConstructionRequirements validConstructionRequirements() {
        HashMap<String, Integer> requiredResources = new HashMap<String, Integer>() {{
            put(REQUIRED_RESOURCE, 0);
        }};
        return ConstructionRequirements.builder()
            .requiredWorkPoints(REQUIRED_WORK_POINTS)
            .requiredResources(requiredResources)
            .build();
    }
}