package com.github.saphyra.apphub.service.elite_base.message_processing.processor;

import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.service.elite_base.message_handling.dao.EdMessage;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.star_system.StarType;
import com.github.saphyra.apphub.service.elite_base.message_processing.saver.StarSystemSaver;
import com.github.saphyra.apphub.service.elite_base.message_processing.structure.navroute.NavrouteMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class NavrouteMessageProcessor implements MessageProcessor {
    private final ObjectMapperWrapper objectMapperWrapper;
    private final StarSystemSaver starSystemSaver;

    @Override
    public boolean canProcess(EdMessage message) {
        return SchemaRefs.NAVROUTE.equals(message.getSchemaRef());
    }

    @Override
    public void processMessage(EdMessage message) {
        NavrouteMessage navrouteMessage = objectMapperWrapper.readValue(message.getMessage(), NavrouteMessage.class);

        Arrays.stream(navrouteMessage.getRoute())
            .forEach(waypoint -> starSystemSaver.save(
                navrouteMessage.getTimestamp(),
                waypoint.getStarId(),
                waypoint.getStarName(),
                waypoint.getStarPosition(),
                StarType.parse(waypoint.getStarType())
            ));
    }
}
