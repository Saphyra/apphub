package com.github.saphyra.apphub.ci.value;

public enum Technology {
    POSTGRES,
    DYNAMODB,
    RABBITMQ;

    public static final Technology[] MINIKUBE = new Technology[]{POSTGRES, DYNAMODB, RABBITMQ};
}
