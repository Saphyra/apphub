package com.github.saphyra.apphub.service.feature.elite_base.dao.fleet_carrier;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.SCHEMA;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.TABLE_FLEET_CARRIER_V2;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(schema = SCHEMA, name = TABLE_FLEET_CARRIER_V2)
public class FleetCarrierEntity {
    @Id
    private String id;
    private String carrierId;
    private String carrierName;
    private String starSystemId;
    @Enumerated(EnumType.STRING)
    private FleetCarrierDockingAccess dockingAccess;
    private Long marketId;
}
