package com.github.saphyra.apphub.lib.error_handler;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({ErrorTranslationConfiguration.class})
public @interface EnableErrorTranslation {
}