package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.construction;

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
@Table(schema = "skyxplore_game", name = "construction")
class ConstructionEntity {
    @Id
    private String constructionId;
    private String gameId;
    private String location;
    private String locationType;
    private Integer requiredWorkPoints;
    private Integer currentWorkPoints;
    private Integer priority;
}
