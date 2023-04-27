package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.priority;

import jakarta.persistence.Column;
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
@Table(schema = "skyxplore_game", name = "priority")
public class PriorityEntity {
    @Id
    private String priorityId;
    private String gameId;
    private String location;
    private String priorityType;
    @Column(name = "priority_value")
    private int value;
}
