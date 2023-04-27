package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.planet;

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
@Table(schema = "skyxplore_game", name = "planet")
class PlanetEntity {
    @Id
    private String planetId;
    private String gameId;
    private String solarSystemId;
    private String defaultName;
    private String customNames;
    private double orbitRadius;
    private double orbitSpeed;
    private int size;
    private String owner;
}
