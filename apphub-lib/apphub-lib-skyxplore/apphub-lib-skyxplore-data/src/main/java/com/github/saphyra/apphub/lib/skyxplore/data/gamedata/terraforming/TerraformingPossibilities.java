package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.terraforming;

import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class TerraformingPossibilities extends ArrayList<TerraformingPossibility> {
    public TerraformingPossibilities(List<TerraformingPossibility> terraformingPossibilities) {
        addAll(terraformingPossibilities);
    }
}
