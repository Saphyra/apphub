package com.github.saphyra.apphub.service.skyxplore.data.save_game.alliance;

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
@Table(schema = "skyxplore_game", name = "alliance")
class AllianceEntity {
    @Id
    private String allianceId;
    private String gameId;
    private String name;
}
