package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.planet;

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
@Table(schema = "skyxplore_game", name = "planet")
class PlanetEntity {
    @Id
    private String planetId;
    private String gameId;
    private String solarSystemId;
    private String defaultName;
    private String customNames;
    private int size;
    private String owner;
}
