package com.kakaopay.pete.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class Application {
    private static Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        log.info("start application.");

        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < 1000000; i ++) {
            list.add(String.valueOf(i));
        }
        log.info("indexOf = {}", list.indexOf("100000"));

    }
}
