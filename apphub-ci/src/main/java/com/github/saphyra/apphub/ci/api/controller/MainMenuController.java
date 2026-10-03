package com.github.saphyra.apphub.ci.api.controller;

import com.github.saphyra.apphub.ci.task_queue.TaskQueue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import static com.github.saphyra.apphub.ci.api.ApiConstants.PARAM_SUCCESS;
import static com.github.saphyra.apphub.ci.api.ApiConstants.REDIRECT;

@Controller
@RequiredArgsConstructor
class MainMenuController {
    private final TaskQueue taskQueue;

    @GetMapping("/")
    String mainMenu() {
        return REDIRECT + "v2";
    }

    @GetMapping("/v2")
    ModelAndView v2MainMenu(
        @RequestParam(value = PARAM_SUCCESS, required = false) String success
    ) {
        ModelAndView mav = new ModelAndView("v2_index");

        if (success != null) {
            mav.addObject(PARAM_SUCCESS, success);
        }

        return mav;
    }

    @GetMapping("/exit")
    String exit() {
        taskQueue.add(() -> System.exit(0));
        return REDIRECT + "v2?success=Shutdown+scheduled";
    }
}
