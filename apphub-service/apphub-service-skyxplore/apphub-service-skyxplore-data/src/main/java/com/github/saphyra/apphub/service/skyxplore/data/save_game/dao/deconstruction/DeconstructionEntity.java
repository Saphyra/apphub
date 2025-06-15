package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.deconstruction;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(schema = "skyxplore_game", name = "deconstruction")
class DeconstructionEntity {
    @Id
    private String deconstructionId;
    private String gameId;
    private String externalReference;
    private String location;
    private Integer priority;
}
