package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.system_connection;

import com.github.saphyra.apphub.service.skyxplore.data.common.LineEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
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

    @OneToOne(cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private LineEntity line;
}
