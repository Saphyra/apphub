package com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.last_update;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Embeddable
public class LastUpdateId implements Serializable {
    private String externalReference;
    @Enumerated(EnumType.STRING)
    private EntityType type;
}
