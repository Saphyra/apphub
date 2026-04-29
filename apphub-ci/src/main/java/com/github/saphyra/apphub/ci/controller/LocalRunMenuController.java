package com.github.saphyra.apphub.ci.controller;

import com.github.saphyra.apphub.ci.dao.PropertyDao;
import com.github.saphyra.apphub.ci.dao.PropertyName;
import com.github.saphyra.apphub.ci.process.local.run_tests.LocalRunTestsProcess;
import com.github.saphyra.apphub.ci.process.local.start.LocalBuildTask;
import com.github.saphyra.apphub.ci.process.local.start.LocalStartProcess;
import com.github.saphyra.apphub.ci.process.local.stop.LocalStopProcess;
import com.github.saphyra.apphub.ci.task_queue.TaskQueue;
import com.github.saphyra.apphub.ci.value.Service;
import com.github.saphyra.apphub.ci.value.Services;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.List;

import static java.util.Objects.nonNull;

@Controller
@RequiredArgsConstructor
@RequestMapping("/local-run")
class LocalRunMenuController {
    private final PropertyDao propertyDao;
    private final LocalBuildTask localBuildTask;
    private final TaskQueue taskQueue;
    private final LocalStartProcess localStartProcess;
    private final Services services;
    private final LocalRunTestsProcess localRunTestsProcess;
    private final LocalStopProcess localStopProcess;

    @GetMapping
    ModelAndView localRunMenu(
        @RequestParam(name = "error", required = false) String error,
        @RequestParam(name = "success", required = false) String success
    ) {
        ModelAndView modelAndView = new ModelAndView("local_run");

        modelAndView.addObject("latest_services", String.join(",", propertyDao.getLatestServices()));
        modelAndView.addObject("latest_test_groups", propertyDao.getLatestTestGroups());
        modelAndView.addObject("task_queue_size", taskQueue.size());
        if (nonNull(error)) {
            modelAndView.addObject("error", error);
        }
        if (nonNull(success)) {
            modelAndView.addObject("success", success);
        }
        return modelAndView;
    }

    @GetMapping("/build")
    String build() {
        taskQueue.add(localBuildTask::buildServices);

        return "redirect:/local-run?success=build_started";
    }

    @GetMapping("/start")
    String start() {
        taskQueue.add(localStartProcess::run);

        return "redirect:/local-run?success=service_startup_initiated";
    }

    @PostMapping("/start-services")
    String startServices(@RequestParam("value") String input) {
        List<String> serviceNames = Arrays.asList(input.split(","));
        List<String> availableServiceNames = services.getServices()
            .stream()
            .map(Service::getName)
            .toList();
        if (serviceNames.stream().anyMatch(serviceName -> !availableServiceNames.contains(serviceName))) {
            return "redirect:/local-run?error=service_not_found";
        }
        propertyDao.save(PropertyName.LATEST_SERVICES, serviceNames);

        taskQueue.add(() -> localStartProcess.startServices(serviceNames));

        return "redirect:/local-run?success=selected_services_are_starting";
    }

    @GetMapping("/run-tests")
    String runTests(){
        taskQueue.add(localRunTestsProcess::run);

        return "redirect:/local-run?success=integration_tests_started";
    }

    @PostMapping("/run-test-groups")
    String runTestGroups(@RequestParam("value") String input) {
        propertyDao.save(PropertyName.LATEST_TEST_GROUPS, input);

        taskQueue.add(() -> localRunTestsProcess.run(input));

        return "redirect:/local-run?success=integration_tests_started";
    }

    @GetMapping("/stop")
    String stop() {
        taskQueue.add(localStopProcess::stopAllServices);

        return "redirect:/local-run?success=services_are_stopping";
    }

    @PostMapping("/stop-services")
    String stopServices(@RequestParam("value") String input) {
        List<String> serviceNames = Arrays.asList(input.split(","));
        List<String> availableServiceNames = services.getServices()
            .stream()
            .map(Service::getName)
            .toList();
        if (serviceNames.stream().anyMatch(serviceName -> !availableServiceNames.contains(serviceName))) {
            return "redirect:/local-run?error=service_not_found";
        }
        propertyDao.save(PropertyName.LATEST_SERVICES, serviceNames);

        taskQueue.add(() -> localStopProcess.stopServices(serviceNames));

        return "redirect:/local-run?success=services_are_stopping";
    }
}
