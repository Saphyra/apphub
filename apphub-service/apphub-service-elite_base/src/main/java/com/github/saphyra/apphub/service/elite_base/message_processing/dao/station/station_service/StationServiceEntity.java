package com.github.saphyra.apphub.service.elite_base.message_processing.dao.station.station_service;

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
@Table(schema = "elite_base", name = "station_service")
class StationServiceEntity {
    @Id
    private String id;
    private String stationId;
    @Enumerated(EnumType.STRING)
    private StationServiceEnum service;
}
