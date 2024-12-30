package com.github.saphyra.service.elite_base.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.zeromq.channel.ZeroMqChannel;
import org.zeromq.ZContext;

@Configuration
@EnableIntegration
@Slf4j
public class ZeroMqConfig {
    @Bean
    public ZContext zContext() {
        return new ZContext();
    }

    @Bean(name = "zeroMqChannel")
    ZeroMqChannel zeroMqPubSubChannel(ZContext context) {
        ZeroMqChannel channel = new ZeroMqChannel(context, true);
        channel.setConnectUrl("tcp://eddn.edcd.io:9500:9500");
        return channel;
    }
}
