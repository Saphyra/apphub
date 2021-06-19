package com.github.saphyra.apphub.service.skyxplore.data.save_game.surface;

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
@Table(schema = "skyxplore_game", name = "surface")
class SurfaceEntity {
    @Id
    private String surfaceId;
    private String gameId;
    private String planetId;
    private String surfaceType;

    @OneToOne(cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private CoordinateEntity coordinate;
}
