package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.line;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
