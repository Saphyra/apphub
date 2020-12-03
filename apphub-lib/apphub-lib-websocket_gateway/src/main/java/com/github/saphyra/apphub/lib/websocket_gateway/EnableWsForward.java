package com.github.saphyra.apphub.lib.websocket_gateway;

import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * set up websocket forward configuration
 *
 * @author Jeffrey
 * @see WsForwardConfiguration
 * @since 2017/12/04 10:35
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(WsForwardConfiguration.class)
public @interface EnableWsForward {

}
