package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.surface;

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
@Table(schema = "skyxplore_game", name = "surface")
class SurfaceEntity {
    @Id
    private String surfaceId;
    private String gameId;
    private String planetId;
    private String surfaceType;
}
