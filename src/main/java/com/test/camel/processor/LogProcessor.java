package com.test.camel.processor;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.io.InputStream;

import static java.util.Objects.isNull;

//Test implementation of camel Processor
@Slf4j
@Component
public class LogProcessor implements Processor {

    @Override
    @SneakyThrows
    public void process(Exchange exchange) {
        Object body = exchange.getIn().getBody();
        if(isNull(body)){
            log.info("Body is null");
            return;
        }
        if (body instanceof InputStream) {
            InputStream is = (InputStream) body;
            is.reset();
            log.info("Log processor >>> InputStream Body: {}", new String(is.readAllBytes()));
            is.reset();
            return;
        }
        if (body instanceof byte[]) {
            byte[] bytes = (byte[]) body;
            log.info("Log processor >>> Byte[] Body: {}", new String(bytes));
            return;
        }
        log.info("Log processor >>> Body: {}", body);
    }
}
