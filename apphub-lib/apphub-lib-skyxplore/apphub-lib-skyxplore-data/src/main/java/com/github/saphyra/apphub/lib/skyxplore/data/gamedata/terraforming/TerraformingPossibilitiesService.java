package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.terraforming;

import com.github.saphyra.apphub.lib.common_util.collection.OptionalMap;
import com.github.saphyra.apphub.lib.data.ValidationAbstractDataService;
import com.github.saphyra.apphub.lib.data.loader.ContentLoaderFactory;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.ConstructionRequirementsProvider;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.ConstructionRequirements;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import jakarta.annotation.PostConstruct;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TerraformingPossibilitiesService extends ValidationAbstractDataService<SurfaceType, TerraformingPossibilities> implements OptionalMap<SurfaceType, TerraformingPossibilities>, ConstructionRequirementsProvider {
    public TerraformingPossibilitiesService(ContentLoaderFactory contentLoaderFactory, TerraformingPossibilitiesValidator terraformingPossibilitiesValidator) {
        super("/data/terraforming_possibilities", contentLoaderFactory, terraformingPossibilitiesValidator);
    }

    @Override
    @PostConstruct
    public void init() {
        super.load(TerraformingPossibilities.class);
    }

    @Override
    public void addItem(TerraformingPossibilities content, String fileName) {
        put(SurfaceType.parse(FilenameUtils.removeExtension(fileName)), content);
    }

    @Override
    public List<ConstructionRequirements> getConstructionRequirements() {
        return values()
            .stream()
            .flatMap(terraformingPossibilities -> terraformingPossibilities.stream().map(TerraformingPossibility::getConstructionRequirements))
            .collect(Collectors.toList());
    }
}
