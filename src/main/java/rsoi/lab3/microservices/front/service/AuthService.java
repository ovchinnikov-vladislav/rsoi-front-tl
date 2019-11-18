package rsoi.lab3.microservices.front.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import rsoi.lab3.microservices.front.entity.User;
import rsoi.lab3.microservices.front.model.RequestUser;

@Service
public class AuthService {
    @Autowired
    private RestTemplate restTemplate;

    public User check(User user) {
        RequestUser requestUser = new RequestUser();
        requestUser.setEmail(user.getUserName());
        requestUser.setPassword(user.getPassword());
        requestUser.setUserName(user.getUserName());
        return restTemplate.postForObject("http://localhost:8080/gate/users/check", requestUser, User.class);
    }

    public User add(User user) {
        return restTemplate.postForObject("http://localhost:8080/gate/users", user, User.class);
    }
}