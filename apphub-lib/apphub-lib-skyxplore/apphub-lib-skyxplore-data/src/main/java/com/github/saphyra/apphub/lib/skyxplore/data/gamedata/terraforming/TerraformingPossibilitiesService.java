package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.terraforming;

import com.github.saphyra.apphub.lib.common_util.collection.OptionalMap;
import com.github.saphyra.apphub.lib.data.ValidationAbstractDataService;
import com.github.saphyra.apphub.lib.data.loader.ContentLoaderFactory;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class TerraformingPossibilitiesService extends ValidationAbstractDataService<SurfaceType, TerraformingPossibilities> implements OptionalMap<SurfaceType, TerraformingPossibilities> {
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
}
