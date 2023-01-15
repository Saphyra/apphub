package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.alliance;

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
@Table(schema = "skyxplore_game", name = "alliance")
class AllianceEntity {
    @Id
    private String allianceId;
    private String gameId;
    private String name;
}
