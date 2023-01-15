package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.universe;

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
@Table(schema = "skyxplore_game", name = "universe")
class UniverseEntity {
    @Id
    private String gameId;
    private int size;
}
