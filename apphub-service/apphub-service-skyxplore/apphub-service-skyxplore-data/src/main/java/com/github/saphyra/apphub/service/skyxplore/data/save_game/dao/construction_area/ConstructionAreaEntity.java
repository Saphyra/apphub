package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.construction_area;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(schema = "skyxplore_game", name = "construction_area")
public class ConstructionAreaEntity {
    @Id
    private String constructionAreaId;
    private String gameId;
    private String location;
    private String surfaceId;
    private String dataId;
}
