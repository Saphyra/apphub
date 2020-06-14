package com.github.saphyra.apphub.lib.request_validation.locale;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({LocaleMandatoryFilterConfiguration.class})
public @interface EnableLocalMandatoryRequestValidation {
}
