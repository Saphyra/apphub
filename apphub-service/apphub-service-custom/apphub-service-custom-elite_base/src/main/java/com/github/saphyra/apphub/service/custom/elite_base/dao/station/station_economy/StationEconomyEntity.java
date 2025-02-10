package com.github.saphyra.apphub.service.custom.elite_base.dao.station.station_economy;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(schema = "elite_base", name = "station_economy")
class StationEconomyEntity {
    @EmbeddedId
    private StationEconomyEntityId id;
    private Double proportion;
}
