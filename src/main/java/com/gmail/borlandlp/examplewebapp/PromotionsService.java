package com.gmail.borlandlp.examplewebapp;

import org.springframework.beans.factory.annotation.PostConstruct;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.stereotype.Component;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

@Component
public class PromotionsService implements BeanNameAware, ApplicationListener<ContextClosedEvent> {
    private String beanName;

    @Override
    public void setBeanName(String name) {
        beanName = name;
    }

    public String getBeanName() {
        return beanName;
    }

    @PostConstruct
    public void test() {
        System.out.println("Hello, PostConstruct!");
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        System.out.println("Hello, ContextClosedEvent!");
    }
}
