package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.planet;

import com.github.saphyra.apphub.service.skyxplore.data.common.CoordinateEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
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

    @OneToOne(cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private CoordinateEntity coordinate;
    private int size;
    private String owner;
}
