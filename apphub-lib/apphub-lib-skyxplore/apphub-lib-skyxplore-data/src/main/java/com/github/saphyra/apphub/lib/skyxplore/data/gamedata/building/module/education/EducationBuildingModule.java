package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.education;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.BuildingModule;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class EducationBuildingModule extends BuildingModule {
    private Integer capacity;
    private List<Education> educations;
}
