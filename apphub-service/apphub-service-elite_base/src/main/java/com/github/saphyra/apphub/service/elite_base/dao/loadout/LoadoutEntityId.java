package com.github.saphyra.apphub.service.elite_base.dao.loadout;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class LoadoutEntityId implements Serializable {
    @Id
    private String externalReference;
    @Id
    @Enumerated(EnumType.STRING)
    private LoadoutType type;
    @Id
    private String name;
}
