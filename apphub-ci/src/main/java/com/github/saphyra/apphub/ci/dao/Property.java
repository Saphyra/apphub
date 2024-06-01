package com.github.saphyra.apphub.ci.dao;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "property")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Property {
    @Id
    @Enumerated(EnumType.STRING)
    private PropertyName name;
    private String value;
}
