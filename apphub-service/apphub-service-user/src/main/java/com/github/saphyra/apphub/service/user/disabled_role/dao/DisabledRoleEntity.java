package com.github.saphyra.apphub.service.user.disabled_role.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(schema = "apphub_user", name = "disabled_role")
public class DisabledRoleEntity {
    @Id
    @Column(name = "disabled_role")
    private String role;
}
