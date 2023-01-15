package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.system_connection;

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
@Table(schema = "skyxplore_game", name = "system_connection")
class SystemConnectionEntity {
    @Id
    private String systemConnectionId;
    private String gameId;
}
