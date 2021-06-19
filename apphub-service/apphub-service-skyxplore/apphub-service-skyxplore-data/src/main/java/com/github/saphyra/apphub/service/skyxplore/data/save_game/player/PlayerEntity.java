package com.github.saphyra.apphub.service.skyxplore.data.save_game.player;

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
