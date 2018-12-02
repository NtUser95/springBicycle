package com.gmail.borlandlp.examplewebapp;

import org.springframework.beans.factory.BeanFactory;

public class Main {
    public static void main(String[] args) {
        BeanFactory beanFactory = new BeanFactory();
        System.out.println("hello world!");
        beanFactory.instantiate("com.gmail.borlandlp.examplewebapp");
        ProductService productService = (ProductService) beanFactory.getBean("productService");
        System.out.println(productService);//ProductService@612
    }
}
