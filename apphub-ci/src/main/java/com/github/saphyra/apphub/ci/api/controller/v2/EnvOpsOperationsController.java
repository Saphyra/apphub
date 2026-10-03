package com.github.saphyra.apphub.ci.api.controller.v2;

import com.github.saphyra.apphub.ci.service.operations.OperationsFacade;
import com.github.saphyra.apphub.ci.task_queue.TaskQueue;
import com.github.saphyra.apphub.ci.value.Environment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.github.saphyra.apphub.ci.api.ApiConstants.PARAM_ENVIRONMENT;
import static com.github.saphyra.apphub.ci.api.ApiConstants.PATH_ENV_OPS;
import static com.github.saphyra.apphub.ci.api.ApiConstants.PATH_ENV_OPS_OPERATIONS;
import static com.github.saphyra.apphub.ci.api.ApiConstants.PATH_VARIABLE_ENVIRONMENT;
import static com.github.saphyra.apphub.ci.api.ApiConstants.REDIRECT;

@Controller
@RequiredArgsConstructor
@RequestMapping(PATH_ENV_OPS_OPERATIONS)
class EnvOpsOperationsController {
    private final OperationsFacade operationsFacade;
    private final TaskQueue taskQueue;

    @PostMapping("/port-forward")
    String portForward(@PathVariable(PARAM_ENVIRONMENT) Environment environment) {
        taskQueue.add(() -> operationsFacade.portForward(environment));

        return REDIRECT + PATH_ENV_OPS.replace(PATH_VARIABLE_ENVIRONMENT, environment.name()) + "?success=Port+forwarding+added+to+task+queue";
    }

    @PostMapping("/proxy")
    String proxy(@PathVariable(PARAM_ENVIRONMENT) Environment environment) {
        taskQueue.add(() -> operationsFacade.proxy(environment));

        return REDIRECT + PATH_ENV_OPS.replace(PATH_VARIABLE_ENVIRONMENT, environment.name()) + "?success=Proxy+start+added+to+task+queue";
    }
}
