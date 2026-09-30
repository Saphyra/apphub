package com.github.saphyra.apphub.service.user.data.service;

import com.github.saphyra.apphub.service.user.data.dao.user.User;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import com.github.saphyra.apphub.service.user.data.service.validator.UserQueryValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserQueryService {
    private final UserQueryValidator userQueryValidator;
    private final UserDao userDao;

    public List<User> getUsers(String queryString) {
        queryString = Optional.ofNullable(queryString)
            .map(String::toLowerCase)
            .map(String::trim)
            .orElse(null);

        userQueryValidator.validateQuery(queryString);

        return userDao.findByUserIdentifier(queryString)
            .stream()
            .toList();
    }
}
