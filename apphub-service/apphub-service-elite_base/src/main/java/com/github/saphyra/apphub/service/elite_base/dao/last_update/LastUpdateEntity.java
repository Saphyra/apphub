package com.github.saphyra.apphub.service.elite_base.dao.last_update;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(schema = "elite_base", name = "last_update")
class LastUpdateEntity {
    @EmbeddedId
    private LastUpdateId id;
    private String lastUpdate;
}
