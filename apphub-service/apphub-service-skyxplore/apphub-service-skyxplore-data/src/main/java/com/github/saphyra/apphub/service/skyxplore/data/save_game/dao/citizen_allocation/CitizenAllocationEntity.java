package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.citizen_allocation;

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
@Table(schema = "skyxplore_game", name = "citizen_allocation")
class CitizenAllocationEntity {
    @Id
    private String citizenAllocationId;
    private String gameId;
    private String citizenId;
    private String processId;
}
