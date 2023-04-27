package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.process;

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
@Table(schema = "skyxplore_game", name = "process")
public class ProcessEntity {
    @Id
    private String processId;
    private String gameId;
    private String processType;
    private String status;
    private String location;
    private String externalReference;
    private String data;
}
