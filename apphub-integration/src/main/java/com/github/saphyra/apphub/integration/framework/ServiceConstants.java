package com.github.saphyra.apphub.integration.framework;

import com.github.saphyra.apphub.integration.core.TestConfiguration;
import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Map;

import static com.github.saphyra.apphub.integration.framework.Constants.ENVIRONMENT_LOCAL;
import static com.github.saphyra.apphub.integration.framework.Constants.ENVIRONMENT_MINIKUBE;
import static com.github.saphyra.apphub.integration.framework.Constants.ENVIRONMENT_PREPROD;
import static com.github.saphyra.apphub.integration.framework.Constants.ENVIRONMENT_PRODUCTION;

public class ServiceConstants {
    public static final String SERVICE_NAME_EVENT_GATEWAY = "event-gateway";
    public static final String SERVICE_NAME_ADMIN_PANEL = "admin-panel";
    public static final String SERVICE_NAME_MAIN_GATEWAY = "main-gateway";
    public static final String SERVICE_NAME_MODULES = "modules";
    public static final String SERVICE_NAME_NOTEBOOK = "notebook";
    public static final String SERVICE_NAME_SCHEDULER = "scheduler";
    public static final String SERVICE_NAME_SKYXPLORE_DATA = "skyxplore-data";
    public static final String SERVICE_NAME_SKYXPLORE_GAME = "skyxplore-game";
    public static final String SERVICE_NAME_SKYXPLORE_LOBBY = "skyxplore-lobby";
    public static final String SERVICE_NAME_TRAINING = "training";
    public static final String SERVICE_NAME_USER = "user";
    public static final String SERVICE_NAME_COMMUNITY = "community";
    public static final String SERVICE_NAME_CALENDAR = "calendar";
    public static final String SERVICE_NAME_STORAGE = "storage";
    public static final String SERVICE_NAME_ELITE_BASE = "elite-base";
    public static final String SERVICE_NAME_MONITORING = "monitoring";
    public static final String SERVICE_NAME_AUTHORIZATION = "authorization";
    public static final String SERVICE_NAME_TASK_MANAGER = "task-manager";

    public static final List<String> LOCAL_SERVICES = ImmutableList.of(
        SERVICE_NAME_EVENT_GATEWAY,
        SERVICE_NAME_ADMIN_PANEL,
        SERVICE_NAME_MAIN_GATEWAY,
        SERVICE_NAME_MODULES,
        SERVICE_NAME_NOTEBOOK,
        SERVICE_NAME_SCHEDULER,
        SERVICE_NAME_SKYXPLORE_DATA,
        SERVICE_NAME_SKYXPLORE_GAME,
        SERVICE_NAME_SKYXPLORE_LOBBY,
        SERVICE_NAME_TRAINING,
        SERVICE_NAME_USER,
        SERVICE_NAME_COMMUNITY,
        SERVICE_NAME_CALENDAR,
        SERVICE_NAME_STORAGE,
        SERVICE_NAME_ELITE_BASE,
        SERVICE_NAME_MONITORING,
        SERVICE_NAME_AUTHORIZATION,
        SERVICE_NAME_TASK_MANAGER
    );
    public static final List<String> MINIKUBE_SERVICES = LOCAL_SERVICES;

    public static final List<String> PRODUCTION_SERVICES = ImmutableList.of(
        SERVICE_NAME_EVENT_GATEWAY,
        SERVICE_NAME_ADMIN_PANEL,
        SERVICE_NAME_MAIN_GATEWAY,
        SERVICE_NAME_MODULES,
        SERVICE_NAME_NOTEBOOK,
        SERVICE_NAME_SCHEDULER,
        SERVICE_NAME_USER,
        SERVICE_NAME_CALENDAR,
        SERVICE_NAME_STORAGE,
        SERVICE_NAME_ELITE_BASE,
        SERVICE_NAME_MONITORING,
        SERVICE_NAME_AUTHORIZATION
    );

    public static List<String> PREPROD_SERVICES = PRODUCTION_SERVICES;

    public static Map<String, List<String>> ENVIRONMENT_SERVICES_MAPPING = Map.of(
        ENVIRONMENT_LOCAL, LOCAL_SERVICES,
        ENVIRONMENT_MINIKUBE, MINIKUBE_SERVICES,
        ENVIRONMENT_PREPROD, PREPROD_SERVICES,
        ENVIRONMENT_PRODUCTION, PRODUCTION_SERVICES
    );

    public static List<String> getServicesForEnvironment() {
        return ENVIRONMENT_SERVICES_MAPPING.get(TestConfiguration.ENVIRONMENT);
    }
}
