package rsoi.lab3.microservices.front.controller;

import org.apache.commons.codec.digest.Md5Crypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import rsoi.lab3.microservices.front.entity.Task;
import rsoi.lab3.microservices.front.entity.Test;
import rsoi.lab3.microservices.front.entity.User;
import rsoi.lab3.microservices.front.model.TaskPage;
import rsoi.lab3.microservices.front.service.TaskService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class TaskChangeController {

    @Autowired
    private TaskService taskService;

    @GetMapping(value = "/user/{id}/task")
    public ModelAndView tasksByUser(Model model,
                                    @PathVariable UUID id,
                                    @RequestParam(value = "page") Optional<Integer> page,
                                    @RequestParam(value = "size") Optional<Integer> size, HttpServletRequest request) {
        int currentPage = page.orElse(0);
        int pageSize = size.orElse(9);
        request.getSession().setAttribute("resultTest", null);
        User u = (User) request.getSession().getAttribute("user");
        request.getSession().setAttribute("task", null);
        if (u == null) {
            u = new User();
        } else if (!u.getIdUser().equals(id)) {
            u = new User();
        } else {
            TaskPage tasksPage = taskService.findByUserId(id, currentPage, pageSize);
            model.addAttribute("tasksPage", tasksPage);
        }
        model.addAttribute("uuid", UUID.randomUUID());
        model.addAttribute("user", u);
        return new ModelAndView("tasks_user");
    }

    @GetMapping(value = "/user/{idUser}/task/{idTask}")
    public ModelAndView taskByUserIdAndTaskId(Model model, HttpServletRequest request,
                                              @PathVariable UUID idUser, @PathVariable UUID idTask) {
        request.getSession().setAttribute("resultTest", null);
        User u = (User) request.getSession().getAttribute("user");
        Task t = new Task();
        t.setTest(new Test());
        if (u == null) {
            u = new User();
        } else if (!u.getIdUser().equals(idUser)) {
            u = new User();
        } else {
            try {
                t = taskService.findByUserIdAndTaskId(idUser, idTask);
                if (t != null)
                    request.getSession().setAttribute("image", t.getImage());
            } catch (Exception exc) {
                exc.printStackTrace();
            }
        }
        model.addAttribute("task", t);
        model.addAttribute("user", u);
        return new ModelAndView("task_change");
    }

    @PostMapping(value = "/user/{idUser}/task/{idTask}/upload")
    public String upload(@PathVariable UUID idUser, @PathVariable UUID idTask, @RequestBody MultipartFile file,
                         Model model, HttpServletRequest request) {
        User u = (User) request.getSession().getAttribute("user");
        if (u != null && !u.getIdUser().equals(idUser)) {
            if (!file.isEmpty()) {
                String directory = System.getProperty("user.dir") + File.separator + "files" + File.separator + "image" + File.separator;
                String name = directory + idTask + ".png";
                try {
                    Files.deleteIfExists(Paths.get(name));
                    Files.createDirectories(Paths.get(directory));
                    Files.copy(file.getInputStream(), Paths.get(name));
                    request.getSession().setAttribute("image", idTask + "");
                } catch (Exception e) {
                    return String.format("redirect:/user/%s/task/%s", idUser, idTask);
                }
            }
            return String.format("redirect:/user/%s/task/%s", idUser, idTask);
        }
        return "redirect:/";
    }

    @PostMapping(value = "/user/{idUser}/task/create")
    public String createTask(@PathVariable UUID idUser, @Valid @RequestBody Task task,
                             Model model, HttpServletRequest request) {
        User u = (User) request.getSession().getAttribute("user");
        if (u != null && u.getIdUser().equals(idUser)) {
            String image = (String) request.getSession().getAttribute("image");
            task.setIdUser(idUser);
            if (image != null)
                task.setImage(image);
            task.setCreateDate(new Date());
            task.getTest().setCreateDate(new Date());
            task = taskService.create(idUser, task);
            if (task != null)
                return String.format("redirect:/user/%s/task/%s", task.getIdUser(), task.getIdTask());
            return String.format("redirect:/user/%s/task", idUser);
        }
        return "redirect:/";
    }

    @PostMapping(value = "/user/{idUser}/task/{idTask}/update")
    public String updateTask(@PathVariable UUID idUser, @PathVariable UUID idTask,
                             @Valid @RequestBody Task task, Model model, HttpServletRequest request) {
        User u = (User) request.getSession().getAttribute("user");
        if (u != null && u.getIdUser().equals(idUser)) {
            String image = (String) request.getSession().getAttribute("image");
            if (image != null)
                task.setImage(image);
            task.setIdUser(idUser);
            task.setCreateDate(new Date());
            task.getTest().setCreateDate(new Date());
            taskService.update(idUser, idTask, task);
            return String.format("redirect:/user/%s/task", idUser);
        }
        return "redirect:/";
    }

    @PostMapping(value = "/user/{idUser}/task/{idTask}/delete")
    public String deleteTask(@PathVariable UUID idUser, @PathVariable UUID idTask,
                             Model model, HttpServletRequest request) {
        User u = (User) request.getSession().getAttribute("user");
        if (u != null && u.getIdUser().equals(idUser)) {
            taskService.delete(idUser, idTask);
            return String.format("redirect:/user/%s/task", idUser);
        }
        return "redirect:/";
    }

}
