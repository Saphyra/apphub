package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.convoy;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(schema = "skyxplore_game", name = "convoy")
class ConvoyEntity {
    @Id
    private String convoyId;
    private String gameId;
    private String resourceDeliveryRequestId;
    private Integer capacity;
}
