package com.github.saphyra.apphub.ci.controller;

import com.github.saphyra.apphub.ci.dao.PropertyDao;
import com.github.saphyra.apphub.ci.dao.PropertyName;
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
@RequestMapping("/settings/s3/{environment}")
@RequiredArgsConstructor
public class S3SettingsMenuController {
    private final PropertyDao propertyDao;

    @GetMapping
    ModelAndView s3SettingsMenu(
        @PathVariable("environment") Environment environment,
        @RequestParam(name = "success", required = false) String success
    ) {
        ModelAndView modelAndView = new ModelAndView("s3_settings");

        modelAndView.addObject("environment", environment);

        Map<String, String> s3properties = propertyDao.getEnvironmentSpecificProperties(PropertyName.S3_CONFIGURATION)
            .getOrDefault(environment, Map.of());
        modelAndView.addObject("enabled", s3properties.get("S3_ENABLED"));
        modelAndView.addObject("access_key_id", s3properties.get("S3_ACCESS_KEY_ID"));
        modelAndView.addObject("secret_key", s3properties.get("S3_SECRET_KEY"));
        modelAndView.addObject("bucket_name", s3properties.get("S3_BUCKET_NAME"));

        if (nonNull(success)) {
            modelAndView.addObject("success", success);
        }

        return modelAndView;
    }

    @PostMapping
    String saveSettings(
        @PathVariable("environment") Environment environment,
        @RequestParam(value = "enabled", required = false) Boolean enabled,
        @RequestParam("access_key_id") String accessKeyId,
        @RequestParam("secret_key") String secretKey,
        @RequestParam("bucket_name") String bucketName
    ) {
        Map<String, String> s3properties = Map.of(
            "S3_ENABLED", Boolean.toString(enabled != null && enabled),
            "S3_ACCESS_KEY_ID", accessKeyId,
            "S3_SECRET_KEY", secretKey,
            "S3_BUCKET_NAME", bucketName
        );

        EnvironmentSpecificProperties properties = propertyDao.getEnvironmentSpecificProperties(PropertyName.S3_CONFIGURATION);
        properties.put(environment, s3properties);
        propertyDao.save(PropertyName.S3_CONFIGURATION, properties);

        return "redirect:/settings/s3/" + environment.name() + "?success=saved";
    }

    @GetMapping("/delete")
    String deleteSettings(@PathVariable("environment") Environment environment) {
        EnvironmentSpecificProperties properties = propertyDao.getEnvironmentSpecificProperties(PropertyName.S3_CONFIGURATION);
        properties.remove(environment);
        propertyDao.save(PropertyName.S3_CONFIGURATION, properties);

        return "redirect:/settings/s3/" + environment.name() + "?success=deleted";
    }
}
