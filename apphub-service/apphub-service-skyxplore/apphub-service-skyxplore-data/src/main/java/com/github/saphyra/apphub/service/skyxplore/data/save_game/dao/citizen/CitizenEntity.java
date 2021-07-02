package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.citizen;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

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
    private String locationType;
    private String name;
    private int morale;
    private int satiety;
}
