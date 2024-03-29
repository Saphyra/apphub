package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.citizen;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(schema = "skyxplore_game", name = "citizen")
class CitizenEntity {
    @Id
    private String citizenId;
    private String gameId;
    private String location;
    private String name;
    private int morale;
    private int satiety;
    private String weaponDataId;
    private String meleeWeaponDataId;
}
