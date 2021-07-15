package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.game;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
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
}
