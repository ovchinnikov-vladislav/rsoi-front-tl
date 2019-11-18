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

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
public class TaskExecuteController {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping(value = "/task/{id}")
    public ModelAndView task(Model model, HttpServletRequest request, @PathVariable Long id) {
        User u = (User) request.getSession().getAttribute("user");
        ResultTest r = (ResultTest) request.getSession().getAttribute("resultTest");
        Task t = new Task();
        if (u == null) {
            u = new User();
        } else {
            t = restTemplate.getForObject(String.format("http://localhost:8080/gate/tasks/%d", id), Task.class);
        }
        if (r == null) {
            r = new ResultTest();
        }
        model.addAttribute("user", u);
        model.addAttribute("task", t);
        model.addAttribute("resultTest", r);
        return new ModelAndView("task");
    }

    @PostMapping(value = "/task/{id}")
    public String sendTaskOnExecute(Model model, HttpServletRequest request, @Valid @ModelAttribute Task task, @PathVariable Long id) {
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
    public String executeTask(@Valid @RequestBody ExecuteTaskRequest executeTaskRequest, Model model, HttpServletRequest request) {
        ResultTest resultTest = restTemplate.postForObject("http://localhost:8080/gate/tasks/execute", executeTaskRequest, ResultTest.class);
        request.getSession().setAttribute("resultTest", resultTest);
        return "redirect:/task/" + executeTaskRequest.getIdTask();
    }

}
