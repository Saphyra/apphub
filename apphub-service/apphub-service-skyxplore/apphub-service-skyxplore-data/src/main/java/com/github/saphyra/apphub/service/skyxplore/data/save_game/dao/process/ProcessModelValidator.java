package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.process;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.GameItemValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProcessModelValidator {
    private final GameItemValidator gameItemValidator;

    public void validate(ProcessModel model) {
        gameItemValidator.validate(model);

        ValidationUtil.notNull(model.getProcessType(), "processType");
        ValidationUtil.notNull(model.getStatus(), "status");
        ValidationUtil.notNull(model.getLocation(), "location");
        ValidationUtil.notNull(model.getExternalReference(), "externalReference");
        ValidationUtil.notNull(model.getData(), "data");
    }
}
