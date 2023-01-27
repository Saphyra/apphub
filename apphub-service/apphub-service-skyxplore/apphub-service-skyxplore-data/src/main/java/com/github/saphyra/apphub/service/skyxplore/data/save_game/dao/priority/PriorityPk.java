package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.priority;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class PriorityPk implements Serializable {
    private String location;
    private String priorityType;
}
