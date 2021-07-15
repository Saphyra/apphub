package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.system_connection;

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
@Table(schema = "skyxplore_game", name = "system_connection")
class SystemConnectionEntity {
    @Id
    private String systemConnectionId;
    private String gameId;
}
