package com.github.saphyra.apphub.service.modules.dao.favorite;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "modules", name = "favorite")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class FavoriteEntity {
    @EmbeddedId
    private FavoriteEntityKey key;

    private String favorite;
}
