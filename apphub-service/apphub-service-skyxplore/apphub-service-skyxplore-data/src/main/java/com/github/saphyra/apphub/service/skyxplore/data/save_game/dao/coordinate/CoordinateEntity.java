package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.coordinate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(schema = "skyxplore_game", name = "coordinate")
@Builder
class CoordinateEntity {
    @Id
    private String coordinateId;
    private String gameId;
    private String referenceId;
    private Double x;
    private Double y;
}
