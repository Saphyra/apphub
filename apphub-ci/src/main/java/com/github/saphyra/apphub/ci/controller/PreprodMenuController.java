package com.github.saphyra.apphub.ci.controller;

import com.github.saphyra.apphub.ci.dao.PropertyDao;
import com.github.saphyra.apphub.ci.dao.PropertyName;
import com.github.saphyra.apphub.ci.process.minikube.PortForwardTask;
import com.github.saphyra.apphub.ci.process.minikube.preprod.PreprodDeployProcess;
import com.github.saphyra.apphub.ci.process.minikube.preprod.PreprodRunTestsProcess;
import com.github.saphyra.apphub.ci.process.minikube.preprod.PreprodScaleDownProcess;
import com.github.saphyra.apphub.ci.process.minikube.preprod.PreprodStartProcess;
import com.github.saphyra.apphub.ci.process.minikube.preprod.PreprodStopProcess;
import com.github.saphyra.apphub.ci.process.minikube.preprod.StartPreprodProxyProcess;
import com.github.saphyra.apphub.ci.task_queue.TaskQueue;
import com.github.saphyra.apphub.ci.value.Constants;
import com.github.saphyra.apphub.ci.value.PlatformProperties;
import com.github.saphyra.apphub.ci.value.Service;
import com.github.saphyra.apphub.ci.value.Services;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.List;

import static java.util.Objects.nonNull;

@Controller
@RequestMapping("/preprod")
@RequiredArgsConstructor
class PreprodMenuController {
    private final TaskQueue taskQueue;
    private final PropertyDao propertyDao;
    private final PlatformProperties platformProperties;
    private final PreprodStartProcess preprodStartProcess;
    private final PreprodDeployProcess preprodDeployProcess;
    private final Services services;
    private final PreprodRunTestsProcess preprodRunTestsProcess;
    private final PortForwardTask  portForwardTask;
    private final StartPreprodProxyProcess startPreprodProxyProcess;
    private final PreprodScaleDownProcess preprodScaleDownProcess;
    private final PreprodStopProcess preprodStopProcess;

    @GetMapping
    ModelAndView preprodMenu(
        @RequestParam(name = "error", required = false) String error,
        @RequestParam(name = "success", required = false) String success
    ) {
        ModelAndView modelAndView = new ModelAndView("preprod");

        modelAndView.addObject("task_queue_size", taskQueue.size());
        modelAndView.addObject("latest_services", String.join(",", propertyDao.getLatestServices()));
        modelAndView.addObject("latest_test_groups", propertyDao.getLatestTestGroups());
        modelAndView.addObject("minikube_port", platformProperties.getMinikubePreprodMainGatewayPort());

        if (nonNull(error)) {
            modelAndView.addObject("error", error);
        }
        if (nonNull(success)) {
            modelAndView.addObject("success", success);
        }

        return modelAndView;
    }

    @GetMapping("/start-vm")
    String startVm() {
        taskQueue.add(preprodStartProcess::startServer);

        return "redirect:/preprod?successs=minikube_is_starting";
    }

    @GetMapping("/deploy")
    String deploy() {
        taskQueue.add(preprodDeployProcess::deploy);

        return "redirect:/preprod?successs=deployment_started";
    }

    @PostMapping("/deploy-services")
    String deployServices(@RequestAttribute("value") String input) {
        List<String> serviceNames = Arrays.asList(input.split(","));
        List<String> availableServiceNames = services.getServices()
            .stream()
            .map(Service::getName)
            .toList();
        if (serviceNames.stream().anyMatch(serviceName -> !availableServiceNames.contains(serviceName))) {
            return "redirect:/minikube?error=service_not_found";
        }
        propertyDao.save(PropertyName.LATEST_SERVICES, serviceNames);

        taskQueue.add(() -> preprodDeployProcess.deployServices(serviceNames));

        return "redirect:/preprod?success=selected_services_are_starting";
    }

    @GetMapping("/run-tests")
    String runTests() {
        taskQueue.add(preprodRunTestsProcess::runTests);

        return "redirect:/preprod?success=integration_tests_are_starting";
    }

    @PostMapping("/run-test-groups")
    String runTestGroups(@RequestAttribute("value") String input) {
        propertyDao.save(PropertyName.LATEST_TEST_GROUPS, input);

        taskQueue.add(() -> preprodRunTestsProcess.runTests(input));

        return "redirect:/preprod?success=integration_tests_started";
    }

    @GetMapping("/port-forward")
    String portForward() {
        taskQueue.add(() -> {
            portForwardTask.portForward(Constants.NAMESPACE_NAME_PREPROD, Constants.SERVICE_NAME_MAIN_GATEWAY, platformProperties.getMinikubeDevServerPort(), Constants.SERVICE_PORT);
        });

        return "redirect:/preprod?success=port_forwarding_started";
    }

    @GetMapping("/proxy")
    String proxy() {
        taskQueue.add(startPreprodProxyProcess::startPreprodProxy);

        return "redirect:/preprod?success=proxy_started";
    }

    @GetMapping("/scale")
    String scale() {
        taskQueue.add(preprodScaleDownProcess::scaleDown);

        return "redirect:/preprod?success=namespace_is_scaling_down";
    }

    @GetMapping("/stop-vm")
    String stopVm() {
        taskQueue.add(preprodStopProcess::stopMinikube);

        return "redirect:/minikube?success=minikube_stopped";
    }
}
