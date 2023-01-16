package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.player;

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
@Table(schema = "skyxplore_game", name = "player")
class PlayerEntity {
    @Id
    private String playerId;
    private String gameId;
    private String userId;
    private String allianceId;
    private String username;
    private boolean ai;
}
