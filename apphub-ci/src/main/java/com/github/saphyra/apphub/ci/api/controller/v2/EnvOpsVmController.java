package com.github.saphyra.apphub.ci.api.controller.v2;

import com.github.saphyra.apphub.ci.service.vm.VmFacade;
import com.github.saphyra.apphub.ci.task_queue.TaskQueue;
import com.github.saphyra.apphub.ci.value.Environment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.github.saphyra.apphub.ci.api.ApiConstants.PARAM_ENVIRONMENT;
import static com.github.saphyra.apphub.ci.api.ApiConstants.PATH_ENV_OPS;
import static com.github.saphyra.apphub.ci.api.ApiConstants.PATH_ENV_OPS_VM;
import static com.github.saphyra.apphub.ci.api.ApiConstants.PATH_VARIABLE_ENVIRONMENT;
import static com.github.saphyra.apphub.ci.api.ApiConstants.REDIRECT;

@Controller
@RequiredArgsConstructor
@RequestMapping(PATH_ENV_OPS_VM)
class EnvOpsVmController {
    private final VmFacade vmFacade;
    private final TaskQueue taskQueue;

    @PostMapping("/start")
    String startVm(@PathVariable(PARAM_ENVIRONMENT) Environment environment) {
        taskQueue.add(() -> vmFacade.start(environment));

        return REDIRECT + PATH_ENV_OPS.replace(PATH_VARIABLE_ENVIRONMENT, environment.name()) + "?success=VM+start+added+to+task+queue";
    }

    @PostMapping("/stop")
    String stopVm(@PathVariable(PARAM_ENVIRONMENT) Environment environment) {
        taskQueue.add(() -> vmFacade.stop(environment));

        return REDIRECT + PATH_ENV_OPS.replace(PATH_VARIABLE_ENVIRONMENT, environment.name()) + "?success=VM+stop+added+to+task+queue";
    }

    @PostMapping("/delete-namespace")
    String deleteNamespace(@PathVariable(PARAM_ENVIRONMENT) Environment environment) {
        taskQueue.add(() -> vmFacade.deleteNamespace(environment));

        return REDIRECT + PATH_ENV_OPS.replace(PATH_VARIABLE_ENVIRONMENT, environment.name()) + "?success=Namespace+deletion+added+to+task+queue";
    }
}
