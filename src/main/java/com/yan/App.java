package com.yan;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
    private static Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        logger.info("Hello World!!!");
        try {
            throw new Exception("throw a exception");
        } catch (Exception e) {
            logger.error("报错了", e);
        }
    }
}
