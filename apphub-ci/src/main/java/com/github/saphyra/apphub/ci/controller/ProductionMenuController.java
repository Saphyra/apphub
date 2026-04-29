package com.github.saphyra.apphub.ci.controller;

import com.github.saphyra.apphub.ci.process.minikube.production.ProductionDeployProcess;
import com.github.saphyra.apphub.ci.process.minikube.production.ProductionRunTestsProcess;
import com.github.saphyra.apphub.ci.process.minikube.production.ProductionStartProcess;
import com.github.saphyra.apphub.ci.process.minikube.production.ProductionStopProcess;
import com.github.saphyra.apphub.ci.process.minikube.production.StartProductionProxyProcess;
import com.github.saphyra.apphub.ci.task_queue.TaskQueue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import static java.util.Objects.nonNull;

@Controller
@RequestMapping("/production")
@RequiredArgsConstructor
class ProductionMenuController {
    private final TaskQueue taskQueue;
    private final ProductionStartProcess productionStartProcess;
    private final ProductionDeployProcess productionDeployProcess;
    private final ProductionRunTestsProcess productionRunTestsProcess;
    private final StartProductionProxyProcess startProductionProxyProcess;
    private final ProductionStopProcess productionStopProcess;

    @GetMapping
    ModelAndView productionMenu(
        @RequestParam(name = "error", required = false) String error,
        @RequestParam(name = "success", required = false) String success
    ) {
        ModelAndView modelAndView = new ModelAndView("production");

        modelAndView.addObject("task_queue_size", taskQueue.size());

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
        taskQueue.add(productionStartProcess::startServer);

        return "redirect:/production?successs=minikube_is_starting";
    }

    @GetMapping("/deploy")
    String deploy() {
        taskQueue.add(productionDeployProcess::deploy);

        return "redirect:/production?successs=deployment_started";
    }

    @GetMapping("/run-tests")
    String runTests() {
        taskQueue.add(productionRunTestsProcess::runTests);

        return "redirect:/production?success=integration_tests_are_starting";
    }

    @GetMapping("/proxy")
    String proxy() {
        taskQueue.add(startProductionProxyProcess::startProductionProxy);

        return "redirect:/production?success=proxy_started";
    }

    @GetMapping("/stop-vm")
    String stopVm() {
        taskQueue.add(productionStopProcess::stopMinikube);

        return "redirect:/production?success=minikube_stopped";
    }
}
