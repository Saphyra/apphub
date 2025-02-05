package com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.station.station_service;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(schema = "elite_base", name = "station_service")
@IdClass(StationServiceEntity.class)
class StationServiceEntity implements Serializable {
    @Id
    private String stationId;
    @Id
    @Enumerated(EnumType.STRING)
    private StationServiceEnum service;
}
