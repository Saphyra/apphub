package com.github.saphyra.apphub.lib.skyxplore.data.gamedata;

import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceDataService;
import jakarta.annotation.PostConstruct;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Builder
class ConstructionRequirementResourceValidator {
    private final List<ConstructionRequirementsProvider> constructionRequirementsProviders;
    private final ResourceDataService resourceDataService;

    @PostConstruct
    void validate() {
        constructionRequirementsProviders.stream()
            .flatMap(constructionRequirementsProvider -> constructionRequirementsProvider.getConstructionRequirements().stream())
            .flatMap(constructionRequirements -> constructionRequirements.getRequiredResources().keySet().stream())
            .distinct()
            .forEach(resourceDataId -> ValidationUtil.containsKey(resourceDataId, resourceDataService, "requiredResource=%s".formatted(resourceDataId)));
    }
}
