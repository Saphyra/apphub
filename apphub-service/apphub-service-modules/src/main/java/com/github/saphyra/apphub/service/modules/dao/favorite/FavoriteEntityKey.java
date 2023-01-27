package com.github.saphyra.apphub.service.modules.dao.favorite;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
class FavoriteEntityKey implements Serializable {
    private String userId;
    private String module;
}
