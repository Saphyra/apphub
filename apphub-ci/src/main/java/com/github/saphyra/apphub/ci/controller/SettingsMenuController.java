package com.github.saphyra.apphub.ci.controller;

import com.github.saphyra.apphub.ci.dao.PropertyDao;
import com.github.saphyra.apphub.ci.dao.PropertyName;
import com.github.saphyra.apphub.ci.service.CertificateService;
import com.github.saphyra.apphub.ci.value.Environment;
import com.github.saphyra.apphub.ci.value.EnvironmentSpecificProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Stream;

import static com.github.saphyra.apphub.ci.value.Constants.FTP_HOST;
import static com.github.saphyra.apphub.ci.value.Constants.PSQL_HOST;
import static java.util.Objects.nonNull;

@Controller
@RequestMapping("/settings")
@RequiredArgsConstructor
class SettingsMenuController {
    private final PropertyDao propertyDao;
    private final CertificateService certificateService;

    @GetMapping
    ModelAndView settingsMenu(
        @RequestParam(name = "success", required = false) String success
    ) {
        ModelAndView modelAndView = new ModelAndView("settings");

        modelAndView.addObject("browser_startup_limit", propertyDao.getBrowserStartupLimit());
        modelAndView.addObject("gui_enabled", propertyDao.isGuiEnabled());
        modelAndView.addObject("environments", Arrays.stream(Environment.values()).map(Environment::name).toList());

        EnvironmentSpecificProperties psqlConfig = propertyDao.getEnvironmentSpecificProperties(PropertyName.PSQL_HOST);
        modelAndView.addObject("PSQL_LOCAL", psqlConfig.getForEnvironmentOrDefault(Environment.LOCAL, PSQL_HOST, ""));
        modelAndView.addObject("PSQL_MINIKUBE", psqlConfig.getForEnvironmentOrDefault(Environment.MINIKUBE, PSQL_HOST, ""));
        modelAndView.addObject("PSQL_PREPROD", psqlConfig.getForEnvironmentOrDefault(Environment.PREPROD, PSQL_HOST, ""));
        modelAndView.addObject("PSQL_PRODUCTION", psqlConfig.getForEnvironmentOrDefault(Environment.PRODUCTION, PSQL_HOST, ""));

        EnvironmentSpecificProperties ftpConfig = propertyDao.getEnvironmentSpecificProperties(PropertyName.FTP_HOST);
        modelAndView.addObject("FTP_LOCAL", ftpConfig.getForEnvironmentOrDefault(Environment.LOCAL, FTP_HOST, ""));
        modelAndView.addObject("FTP_MINIKUBE", ftpConfig.getForEnvironmentOrDefault(Environment.MINIKUBE, FTP_HOST, ""));
        modelAndView.addObject("FTP_PREPROD", ftpConfig.getForEnvironmentOrDefault(Environment.PREPROD, FTP_HOST, ""));
        modelAndView.addObject("FTP_PRODUCTION", ftpConfig.getForEnvironmentOrDefault(Environment.PRODUCTION, FTP_HOST, ""));


        if (nonNull(success)) {
            modelAndView.addObject("success", success);
        }

        return modelAndView;
    }

    @PostMapping
    @Transactional
    String saveSettings(HttpServletRequest request) {
        propertyDao.save(PropertyName.BROWSER_STARTUP_LIMIT, Integer.parseInt(request.getParameter("browser_startup_limit")));
        propertyDao.save(PropertyName.GUI_ENABLED, Boolean.parseBoolean(request.getParameter("gui_enabled")));

        return "redirect:/settings?success=saved";
    }

    @GetMapping("/authorization/certificate/test")
    String recreateTestAuthorizationCertificate() {
        Map<String, String> certificates = certificateService.generateKeyPair();

        EnvironmentSpecificProperties certStore = propertyDao.getEnvironmentSpecificProperties(PropertyName.AUTHORIZATION_CERTIFICATE);
        Stream.of(Environment.LOCAL, Environment.MINIKUBE, Environment.PREPROD)
            .forEach(environment -> certStore.put(environment, certificates));

        propertyDao.save(PropertyName.AUTHORIZATION_CERTIFICATE, certStore);

        return "redirect:/settings?success=test_authorization_certificate_recreated";
    }

    @GetMapping("/authorization/certificate/production")
    String recreateProductionAuthorizationCertificate() {
        Map<String, String> certificates = certificateService.generateKeyPair();

        EnvironmentSpecificProperties certStore = propertyDao.getEnvironmentSpecificProperties(PropertyName.AUTHORIZATION_CERTIFICATE);
        certStore.put(Environment.PRODUCTION, certificates);

        propertyDao.save(PropertyName.AUTHORIZATION_CERTIFICATE, certStore);

        return "redirect:/settings?success=production_authorization_certificate_recreated";
    }

    @PostMapping("/psql-host")
    String savePsqlHost(HttpServletRequest request) {
        EnvironmentSpecificProperties properties = propertyDao.getEnvironmentSpecificProperties(PropertyName.PSQL_HOST);

        properties.put(Environment.LOCAL, Map.of(PSQL_HOST, request.getParameter(Environment.LOCAL.name())));
        properties.put(Environment.MINIKUBE, Map.of(PSQL_HOST, request.getParameter(Environment.MINIKUBE.name())));
        properties.put(Environment.PREPROD, Map.of(PSQL_HOST, request.getParameter(Environment.PREPROD.name())));
        properties.put(Environment.PRODUCTION, Map.of(PSQL_HOST, request.getParameter(Environment.PRODUCTION.name())));

        propertyDao.save(PropertyName.PSQL_HOST, properties);

        return "redirect:/settings?success=psql_host_saved";
    }

    @PostMapping("/ftp-host")
    String saveFtpHost(HttpServletRequest request) {
        EnvironmentSpecificProperties properties = propertyDao.getEnvironmentSpecificProperties(PropertyName.FTP_HOST);

        properties.put(Environment.LOCAL, Map.of(FTP_HOST, request.getParameter(Environment.LOCAL.name())));
        properties.put(Environment.MINIKUBE, Map.of(FTP_HOST, request.getParameter(Environment.MINIKUBE.name())));
        properties.put(Environment.PREPROD, Map.of(FTP_HOST, request.getParameter(Environment.PREPROD.name())));
        properties.put(Environment.PRODUCTION, Map.of(FTP_HOST, request.getParameter(Environment.PRODUCTION.name())));

        propertyDao.save(PropertyName.FTP_HOST, properties);

        return "redirect:/settings?success=ftp_host_saved";
    }
}
