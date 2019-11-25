package rsoi.lab3.microservices.front.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import rsoi.lab3.microservices.front.entity.Result;
import rsoi.lab3.microservices.front.entity.Task;
import rsoi.lab3.microservices.front.entity.User;
import rsoi.lab3.microservices.front.model.ExecuteTaskRequest;
import rsoi.lab3.microservices.front.model.ResultTest;
import rsoi.lab3.microservices.front.service.TaskExecutorService;
import rsoi.lab3.microservices.front.service.TaskService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
public class TaskExecuteController {

    @Autowired
    private TaskExecutorService executorService;
    @Autowired
    private TaskService taskService;

    @GetMapping(value = "/task/{id}")
    public ModelAndView task(@PathVariable Long id, Model model, HttpServletRequest request) {
        User u = (User) request.getSession().getAttribute("user");
        ResultTest r = (ResultTest) request.getSession().getAttribute("resultTest");
        Task t = new Task();
        if (u == null) {
            u = new User();
        } else {
            t = taskService.findById(id);
        }
        if (r == null) {
            r = new ResultTest();
        }
        model.addAttribute("user", u);
        model.addAttribute("task", t);
        model.addAttribute("resultTest", r);
        request.getSession().setAttribute("resultTest", null);
        return new ModelAndView("task");
    }

    @PostMapping(value = "/task/{id}")
    public String sendTaskOnExecute(@Valid @ModelAttribute Task task, @PathVariable Long id,
                                    Model model, HttpServletRequest request) {
        User u = (User) request.getSession().getAttribute("user");
        Task t = (Task) request.getSession().getAttribute("task");
        if (u != null && t != null) {
            ExecuteTaskRequest executeTaskRequest = new ExecuteTaskRequest();
            executeTaskRequest.setIdTask(t.getIdTask());
            executeTaskRequest.setIdUser(t.getIdUser());
            executeTaskRequest.setSourceTask(task.getTemplateCode());
        } else {
            u = new User();
            t = new Task();
        }
        return "redirect:/task/" + task.getIdTask();
    }

    @PostMapping(value = "/task/execute")
    public String executeTask(@Valid @RequestBody ExecuteTaskRequest executeTaskRequest,
                              Model model, HttpServletRequest request) {
        ResultTest resultTest = executorService.execute(executeTaskRequest);
        request.getSession().setAttribute("resultTest", resultTest);
        return "redirect:/task/" + executeTaskRequest.getIdTask();
    }

}
