package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.game;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(schema = "skyxplore_game", name = "game")
class GameEntity {
    @Id
    private String gameId;
    private String host;
    private String name;
    private LocalDateTime lastPlayed;
    private Boolean markedForDeletion;
    private LocalDateTime markedForDeletionAt;
    private Integer universeSize;
}
