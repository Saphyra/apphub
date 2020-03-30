package com.github.saphyra.apphub.lib.config.whielist;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({WhiteListedEndpointProperties.class})
public @interface EnableWhiteListedEndpointProperties {
}
