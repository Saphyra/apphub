package com.github.saphyra.apphub.ci.value;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Environment {
    LOCAL(false),
    MINIKUBE(false),
    PREPROD(true),
    PRODUCTION(true),
    DEFAULT(false);

    private final boolean prodLike;
}
