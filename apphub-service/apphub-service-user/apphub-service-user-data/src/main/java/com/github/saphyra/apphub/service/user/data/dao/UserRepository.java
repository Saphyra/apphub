package com.github.saphyra.apphub.service.user.data.dao;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

//TODO unit test
public interface UserRepository extends CrudRepository<UserEntity, String> {
    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByUsername(String username);
}
