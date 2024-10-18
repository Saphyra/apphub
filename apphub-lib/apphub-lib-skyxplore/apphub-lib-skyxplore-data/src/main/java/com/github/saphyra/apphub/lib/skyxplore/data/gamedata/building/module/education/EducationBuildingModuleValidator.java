package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.education;

import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.data.DataValidator;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.BuildingModuleValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
//TODO unit test
class EducationBuildingModuleValidator implements DataValidator<Map<String, EducationBuildingModule>> {
    private final BuildingModuleValidator buildingModuleValidator;
    private final EducationValidator educationValidator;

    @Override
    public void validate(Map<String, EducationBuildingModule> item) {
        item.forEach(this::validate);
    }

    private void validate(String s, EducationBuildingModule educationBuildingModule) {
        buildingModuleValidator.validate(educationBuildingModule);
        educationValidator.validate(educationBuildingModule.getEducations());

        ValidationUtil.atLeast(educationBuildingModule.getCapacity(), 1, "capacity");
    }
}
