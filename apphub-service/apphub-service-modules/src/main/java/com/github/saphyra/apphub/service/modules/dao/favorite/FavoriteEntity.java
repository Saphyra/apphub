package com.github.saphyra.apphub.service.modules.dao.favorite;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

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
