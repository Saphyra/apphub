package com.github.saphyra.apphub.ci.controller;

import com.github.saphyra.apphub.ci.dao.PropertyDao;
import com.github.saphyra.apphub.ci.dao.PropertyName;
import com.github.saphyra.apphub.ci.process.minikube.MinikubeScaleProcess;
import com.github.saphyra.apphub.ci.process.minikube.MinikubeStartProcess;
import com.github.saphyra.apphub.ci.process.minikube.NamespaceNameProvider;
import com.github.saphyra.apphub.ci.process.minikube.PortForwardTask;
import com.github.saphyra.apphub.ci.process.minikube.local.MinikubeLocalDeployProcess;
import com.github.saphyra.apphub.ci.process.minikube.local.MinikubeLocalStopProcess;
import com.github.saphyra.apphub.ci.process.minikube.local.MinikubeNamespaceDeletionProcess;
import com.github.saphyra.apphub.ci.process.minikube.local.MinikubeLocalRunTestsProcess;
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
@RequestMapping("/minikube")
@RequiredArgsConstructor
class MinikubeMenuController {
    private final PropertyDao propertyDao;
    private final TaskQueue taskQueue;
    private final PlatformProperties platformProperties;
    private final MinikubeStartProcess minikubeStartProcess;
    private final MinikubeLocalDeployProcess minikubeLocalDeployProcess;
    private final Services services;
    private final MinikubeLocalRunTestsProcess minikubeLocalRunTestsProcess;
    private final PortForwardTask portForwardTask;
    private final NamespaceNameProvider namespaceNameProvider;
    private final MinikubeScaleProcess minikubeScaleProcess;
    private final MinikubeNamespaceDeletionProcess namespaceDeletionProcess;
    private final MinikubeLocalStopProcess minikubeLocalStopProcess;

    @GetMapping
    ModelAndView minikubeMenu(
        @RequestParam(name = "error", required = false) String error,
        @RequestParam(name = "success", required = false) String success
    ) {
        ModelAndView modelAndView = new ModelAndView("minikube");

        modelAndView.addObject("task_queue_size", taskQueue.size());
        modelAndView.addObject("latest_services", String.join(",", propertyDao.getLatestServices()));
        modelAndView.addObject("latest_test_groups", propertyDao.getLatestTestGroups());
        modelAndView.addObject("minikube_port", platformProperties.getMinikubeDevServerPort());

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
        taskQueue.add(minikubeStartProcess::startMinikube);

        return "redirect:/minikube?success=minikube_is_starting";
    }

    @GetMapping("/deploy")
    String deploy() {
        taskQueue.add(minikubeLocalDeployProcess::deploy);

        return "redirect:/minikube?success=deployment_started";
    }

    @PostMapping("/deploy-services")
    String deployServices(@RequestParam("value") String input) {
        List<String> serviceNames = Arrays.asList(input.split(","));
        List<String> availableServiceNames = services.getServices()
            .stream()
            .map(Service::getName)
            .toList();
        if (serviceNames.stream().anyMatch(serviceName -> !availableServiceNames.contains(serviceName))) {
            return "redirect:/minikube?error=service_not_found";
        }
        propertyDao.save(PropertyName.LATEST_SERVICES, serviceNames);

        taskQueue.add(() -> minikubeLocalDeployProcess.deploy(serviceNames));

        return "redirect:/minikube?success=selected_services_are_starting";
    }

    @GetMapping("/run-tests")
    String runTests() {
        taskQueue.add(minikubeLocalRunTestsProcess::runTests);

        return "redirect:/minikube?success=integration_tests_are_starting";
    }

    @PostMapping("/run-test-groups")
    String runTestGroups(@RequestAttribute("value") String input) {
        propertyDao.save(PropertyName.LATEST_TEST_GROUPS, input);

        taskQueue.add(() -> minikubeLocalRunTestsProcess.runTests(input));

        return "redirect:/minikube?success=integration_tests_started";
    }

    @GetMapping("/port-forward")
    String portForward() {
        taskQueue.add(() -> {
            String namespaceName = namespaceNameProvider.getNamespaceName();

            portForwardTask.portForward(namespaceName, Constants.SERVICE_NAME_MAIN_GATEWAY, platformProperties.getMinikubeDevServerPort(), Constants.SERVICE_PORT);
            portForwardTask.portForward(namespaceName, Constants.SERVICE_NAME_POSTGRES, platformProperties.getMinikubeDatabasePort(), Constants.POSTGRES_PORT);
        });

        return "redirect:/minikube?success=port_forwarding_started";
    }

    @GetMapping("/scale")
    String scale() {
        taskQueue.add(() -> minikubeScaleProcess.scale(namespaceNameProvider.getNamespaceName(), 0));

        return "redirect:/minikube?success=namespace_is_scaling_down";
    }

    @GetMapping("delete-namespace")
    String deleteNamespace() {
        taskQueue.add(() -> {
            String namespaceName = namespaceNameProvider.getNamespaceName();
            namespaceDeletionProcess.deleteNamespace(namespaceName);
        });

        return "redirect:/minikube?success=namespace_deletion_started";
    }

    @GetMapping("/stop-vm")
    String stopVm() {
        taskQueue.add(minikubeLocalStopProcess::stopMinikube);

        return "redirect:/minikube?success=minikube_stopped";
    }
}
