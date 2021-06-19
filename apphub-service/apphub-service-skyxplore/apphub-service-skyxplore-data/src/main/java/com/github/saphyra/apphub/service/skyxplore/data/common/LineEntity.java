package com.github.saphyra.apphub.service.skyxplore.data.common;

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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(schema = "skyxplore_game", name = "line")
public class LineEntity {
    @Id
    private String referenceId;
    private String aId;
    private String bId;

    @OneToOne(cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn(name = "a_id", referencedColumnName = "reference_id")
    private CoordinateEntity a;

    @OneToOne(cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn(name = "b_id", referencedColumnName = "reference_id")
    private CoordinateEntity b;
}
