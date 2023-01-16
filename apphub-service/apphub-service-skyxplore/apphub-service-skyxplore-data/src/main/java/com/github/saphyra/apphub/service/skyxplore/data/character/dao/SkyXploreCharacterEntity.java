package com.github.saphyra.apphub.service.skyxplore.data.character.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(schema = "skyxplore", name = "character")
public class SkyXploreCharacterEntity {
    @Id
    private String userId;
    private String name;
}
