package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.allocated_resource;

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
@Table(schema = "skyxplore_game", name = "allocated_resource")
public class AllocatedResourceEntity {
    @Id
    private String allocatedResourceId;
    private String gameId;
    private String location;
    private String externalReference;
    private String dataId;
    private int amount;
}
