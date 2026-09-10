package com.github.saphyra.apphub.service.feature.elite_base.dao.station.station_service;

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

import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.SCHEMA;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.TABLE_STATION_SERVICE;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(schema = SCHEMA, name = TABLE_STATION_SERVICE)
@IdClass(StationServiceEntity.class)
class StationServiceEntity implements Serializable {
    @Id
    private String stationId;
    @Id
    @Enumerated(EnumType.STRING)
    private StationServiceEnum service;
}
