package com.github.saphyra.apphub.service.user.data.dao.user;

class UserDaoConstants {
     static final String TYPE_PROFILE = "PROFILE";
     static final String TYPE_MARKED_FOR_DELETION = "MARKED_FOR_DELETION";
     static final String TYPE_ROLE = "ROLE";
     static final String TYPE_CREDENTIAL = "CREDENTIAL";
     static final String TYPE_USER_ID = "USER_ID";

     static final String COLUMN_PK = "pk";
     static final String COLUMN_SK = "sk";
     static final String COLUMN_EMAIL = "email";
     static final String COLUMN_USERNAME = "username";
     static final String COLUMN_LANGUAGE = "language";
     static final String COLUMN_PASSWORD = "password";
     static final String COLUMN_PASSWORD_FAILURE_COUNT = "password_failure_count";
     static final String COLUMN_LOCKED_UNTIL = "locked_until";
     static final String COLUMN_MARKED_FOR_DELETION_AT = "marked_for_deletion_at";
     static final String COLUMN_USER_ID = "user_id";

     static final String GSI_MARKED_FOR_DELETION_AT = "marked_for_deletion_at-index";
}
