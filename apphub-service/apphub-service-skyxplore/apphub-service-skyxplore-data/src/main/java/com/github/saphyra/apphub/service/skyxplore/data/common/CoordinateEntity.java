package com.github.saphyra.apphub.service.skyxplore.data.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(schema = "skyxplore_game", name = "coordinate")
public class CoordinateEntity {
    @Id
    private String referenceId;
    private Double x;
    private Double y;
}
