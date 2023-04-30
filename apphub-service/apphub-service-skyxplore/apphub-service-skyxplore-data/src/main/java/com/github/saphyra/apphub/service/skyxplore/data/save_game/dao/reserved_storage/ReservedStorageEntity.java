package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.reserved_storage;

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
@Table(schema = "skyxplore_game", name = "reserved_storage")
class ReservedStorageEntity {
    @Id
    private String reservedStorageId;
    private String location;
    private String gameId;
    private String externalReference;
    private String dataId;
    private int amount;
}
