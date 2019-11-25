package rsoi.lab3.microservices.front.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import rsoi.lab3.microservices.front.model.ExecuteTaskRequest;
import rsoi.lab3.microservices.front.model.ResultTest;

@Service
public class TaskExecutorService {

    @Autowired
    private RestTemplate restTemplate;

    public ResultTest execute(ExecuteTaskRequest executeTaskRequest) {
        return restTemplate.postForObject("http://localhost:8080/gate/tasks/execute", executeTaskRequest, ResultTest.class);
    }
}
