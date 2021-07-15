package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.line;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(schema = "skyxplore_game", name = "line")
class LineEntity {
    @Id
    private String lineId;
    private String gameId;
    private String referenceId;

    @Column(name = "a_id")
    private String a;

    @Column(name = "b_id")
    private String b;
}
