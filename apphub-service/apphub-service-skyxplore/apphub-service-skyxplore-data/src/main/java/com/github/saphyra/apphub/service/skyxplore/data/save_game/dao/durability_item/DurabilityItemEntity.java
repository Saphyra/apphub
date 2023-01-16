package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.durability_item;

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
@Table(schema = "skyxplore_game", name = "durability_item")
class DurabilityItemEntity {
    @Id
    private String durabilityItemId;
    private String gameId;
    private String parent;
    private String metadata;
    private String dataId;
    private Integer maxDurability;
    private Integer currentDurability;
}
