package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.process;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

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
    private String locationType;
    private String externalReference;
    private String data;
}
