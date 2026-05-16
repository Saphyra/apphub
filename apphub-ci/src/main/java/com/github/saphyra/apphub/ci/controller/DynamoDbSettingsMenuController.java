package com.github.saphyra.apphub.ci.controller;

import com.github.saphyra.apphub.ci.dao.PropertyDao;
import com.github.saphyra.apphub.ci.dao.PropertyName;
import com.github.saphyra.apphub.ci.value.Constants;
import com.github.saphyra.apphub.ci.value.Environment;
import com.github.saphyra.apphub.ci.value.EnvironmentSpecificProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

import static java.util.Objects.nonNull;

@Controller
@RequestMapping("/settings/dynamo-db/{environment}")
@RequiredArgsConstructor
public class DynamoDbSettingsMenuController {
    private final PropertyDao propertyDao;

    @GetMapping
    ModelAndView dynamoDbSettingsMenu(
        @PathVariable("environment") Environment environment,
        @RequestParam(name = "success", required = false) String success
    ) {
        ModelAndView modelAndView = new ModelAndView("dynamo_db_settings");

        modelAndView.addObject("environment", environment);

        Map<String, String> properties = propertyDao.getEnvironmentSpecificProperties(PropertyName.DYNAMO_DB_CONFIGURATION)
            .getOrDefault(environment, Map.of());
        modelAndView.addObject("access_key_id", properties.get(Constants.DYNAMO_DB_ACCESS_KEY_ID));
        modelAndView.addObject("secret_key", properties.get(Constants.DYNAMO_DB_SECRET_KEY));

        if (nonNull(success)) {
            modelAndView.addObject("success", success);
        }

        return modelAndView;
    }

    @PostMapping
    String saveSettings(
        @PathVariable("environment") Environment environment,
        @RequestParam("access_key_id") String accessKeyId,
        @RequestParam("secret_key") String secretKey
    ) {
        Map<String, String> dynamoDbProperties = Map.of(
            Constants.DYNAMO_DB_ACCESS_KEY_ID, accessKeyId,
            Constants.DYNAMO_DB_SECRET_KEY, secretKey
        );

        EnvironmentSpecificProperties properties = propertyDao.getEnvironmentSpecificProperties(PropertyName.DYNAMO_DB_CONFIGURATION);
        properties.put(environment, dynamoDbProperties);
        propertyDao.save(PropertyName.DYNAMO_DB_CONFIGURATION, properties);

        return "redirect:/settings/dynamo-db/" + environment.name() + "?success=saved";
    }

    @GetMapping("/delete")
    String deleteSettings(@PathVariable("environment") Environment environment){
        EnvironmentSpecificProperties properties = propertyDao.getEnvironmentSpecificProperties(PropertyName.DYNAMO_DB_CONFIGURATION);
        properties.remove(environment);
        propertyDao.save(PropertyName.DYNAMO_DB_CONFIGURATION, properties);

        return "redirect:/settings/dynamo-db/" + environment.name() + "?success=deleted";
    }
}
