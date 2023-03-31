package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.durability;

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
@Table(schema = "skyxplore_game", name = "durability")
class DurabilityEntity {
    @Id
    private String durabilityId;
    private String gameId;
    private String externalReference;
    private Integer maxHitPoints;
    private Integer currentHitPoints;
}
