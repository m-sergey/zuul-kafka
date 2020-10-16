package sampleservice;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class HelloService {
    public String hello(String name) {

        if(StringUtils.isEmpty(name)) {
            return "Who are you?";
        }

        return "Hello, " + name;
    }
}
