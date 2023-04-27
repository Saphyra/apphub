package com.github.saphyra.apphub.api.skyxplore.model.game;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.UUID;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CitizenModel extends GameItem {
    private UUID location;
    private String name;
    private Integer morale;
    private Integer satiety;
    private String weaponDataId;
    private String meleeWeaponDataId;
}
