package com.test.camel.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.SneakyThrows;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.Map;

import static java.util.Objects.isNull;

@Component
public class HelloWorldProcessor implements Processor {
    @Override
    @SneakyThrows
    public void process(Exchange exchange) {
        var body = exchange.getIn().getBody(Map.class);
        Message outMessage = exchange.getMessage();
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();
        if(isNull(body)){
            objectNode.put("responseText", "Body is null");
            outMessage.setBody(objectNode.toString());
            outMessage.setHeader("Content-Type", "application/json");
            return;
        }
        if (body.containsKey("name")) {
            objectNode.put("responseText", "Hello " + body.get("name"));
        } else {
            objectNode.put("responseText", "Hello unknown");
        }
        outMessage.setBody(objectNode.toString());
        outMessage.setHeader("Content-Type", "application/json");
    }
}
