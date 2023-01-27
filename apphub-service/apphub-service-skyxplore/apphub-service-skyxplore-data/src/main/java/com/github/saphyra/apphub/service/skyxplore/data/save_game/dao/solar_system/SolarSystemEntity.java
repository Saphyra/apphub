package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.solar_system;

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
@Table(schema = "skyxplore_game", name = "solar_system")
class SolarSystemEntity {
    @Id
    private String solarSystemId;
    private String gameId;
    private int radius;
    private String defaultName;
    private String customNames;
}
