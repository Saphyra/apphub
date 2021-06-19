package com.github.saphyra.apphub.service.skyxplore.data.save_game.priority;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(schema = "skyxplore_game", name = "priority")
public class PriorityEntity {
    @EmbeddedId
    private PriorityPk pk;
    private String gameId;
    private String locationType;
    private int value;
}
