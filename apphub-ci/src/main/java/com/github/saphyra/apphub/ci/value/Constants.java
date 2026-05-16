package com.github.saphyra.apphub.ci.value;

public class Constants {
    //Services
    public static final String SERVICE_NAME_MAIN_GATEWAY = "main-gateway";
    public static final String SERVICE_NAME_POSTGRES = "postgres";
    public static final String SERVICE_NAME_DYNAMO_DB = "dynamodb";

    //Ports
    public static final int SERVICE_PORT = 8080;
    public static final int POSTGRES_PORT = 5432;

    public static final String DIR_NAME_DEVELOP = "develop";
    public static final String DIR_NAME_PRODUCTION = "production";
    public static final String DIR_NAME_PREPROD = "preprod";
    public static final String NAMESPACE_NAME_PRODUCTION = "production";
    public static final String NAMESPACE_NAME_PREPROD = "preprod";
    public static final String PROFILE_PREPROD = "preprod";
    public static final String PROFILE_LOCAL = "local";

    public static final String PSQL_HOST = "PSQL_HOST";
    public static final String FTP_HOST = "FTP_HOST";

    //Property keys
    public static final String DYNAMO_DB_ACCESS_KEY_ID = "DYNAMO_DB_ACCESS_KEY_ID";
    public static final String DYNAMO_DB_SECRET_KEY = "DYNAMO_DB_SECRET_KEY";
}
