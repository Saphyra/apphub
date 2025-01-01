package com.github.saphyra.apphub.service.elite_base.dao.fleet_carrier;

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
@Table(schema = "elite_base", name = "fleet_carrier")
public class FleetCarrierEntity {
    @Id
    private String id;
    private String carrierId;
    private String lastUpdate;
    private String carrierName;
    private String starSystemId;
    @Enumerated(EnumType.STRING)
    private FleetCarrierDockingAccess dockingAccess;
    private Long marketId;
}
