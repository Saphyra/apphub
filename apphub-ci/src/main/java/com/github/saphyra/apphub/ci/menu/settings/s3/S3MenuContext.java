package com.github.saphyra.apphub.ci.menu.settings.s3;

import com.github.saphyra.apphub.ci.dao.PropertyDao;
import com.github.saphyra.apphub.ci.utils.ApplicationContextProxy;
import com.github.saphyra.apphub.ci.utils.BooleanParser;
import com.github.saphyra.apphub.ci.utils.ValidatingInputReader;
import com.github.saphyra.apphub.ci.value.Environment;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@RequiredArgsConstructor
public class S3MenuContext {
    static final List<String> PROPERTIES = List.of(
        "S3_ENABLED",
        "S3_ACCESS_KEY_ID",
        "S3_SECRET_KEY",
        "S3_BUCKET_NAME"
    );

    private Environment environment;

    private final ApplicationContextProxy applicationContextProxy;
    private final PropertyDao propertyDao;
    private final ValidatingInputReader validatingInputReader;
    private final BooleanParser booleanParser;
}
