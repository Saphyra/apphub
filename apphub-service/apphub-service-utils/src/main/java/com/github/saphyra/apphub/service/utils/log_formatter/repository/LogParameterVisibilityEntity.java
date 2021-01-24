package com.github.saphyra.apphub.service.utils.log_formatter.repository;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(schema = "utils", name = "log_parameter_visibility")
class LogParameterVisibilityEntity {
    @Id
    private String id;
    private String userId;
    private String parameter;
    private String visible;
}
