package sampleservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

    @Autowired
    HelloService service;

    @RequestMapping(method = RequestMethod.GET, path = "/hello")
    public ResponseEntity<String> hello(@RequestParam(required = false) String name) {
        return new ResponseEntity<>(service.hello(name), HttpStatus.OK);
    }
}
