package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.construction;

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
@Table(schema = "skyxplore_game", name = "construction")
class ConstructionEntity {
    @Id
    private String constructionId;
    private String gameId;
    private String externalReference;
    private Integer parallelWorkers;
    private Integer requiredWorkPoints;
    private Integer currentWorkPoints;
    private Integer priority;
    private String data;
}
