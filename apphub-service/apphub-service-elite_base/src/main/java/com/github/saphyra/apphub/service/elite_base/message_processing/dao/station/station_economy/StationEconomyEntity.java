package com.github.saphyra.apphub.service.elite_base.message_processing.dao.station.station_economy;

import com.github.saphyra.apphub.service.elite_base.message_processing.dao.EconomyEnum;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
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
    @Id
    private String id;
    private String stationId;
    @Enumerated(EnumType.STRING)
    private EconomyEnum economy;
    private Double proportion;
}
