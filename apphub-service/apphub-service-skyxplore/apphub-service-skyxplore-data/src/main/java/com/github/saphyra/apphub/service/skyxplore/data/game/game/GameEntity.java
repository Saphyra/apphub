package com.github.saphyra.apphub.service.skyxplore.data.game.game;

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
@Table(schema = "skyxplore_game", name = "game")
class GameEntity {
    @Id
    private String gameId;
    private String host;
    private String name;
}
