package com.gmail.borlandlp.examplewebapp;

import com.gmail.borlandlp.examplewebapp.processors.CustomPostProcessor;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;

public class Main {
    public static void main(String[] args) {
        //https://habr.com/post/419679/
        try {
            ApplicationContext applicationContext = new ApplicationContext("com.gmail.borlandlp.examplewebapp");
            applicationContext.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*BeanFactory beanFactory = new BeanFactory();

        beanFactory.instantiate("com.gmail.borlandlp.examplewebapp");
        beanFactory.addPostProcessor(new CustomPostProcessor());
        beanFactory.populateProperties();
        beanFactory.injectBeanNames();
        beanFactory.initializeBeans();

        ProductService productService = (ProductService) beanFactory.getBean("productService");
        System.out.println(productService);//ProductService@612
        System.out.println(productService.getPromotionsService());
        PromotionsService promotionsService = (PromotionsService) beanFactory.getBean("promotionsService");
        System.out.println("Bean name = " + promotionsService.getBeanName());*/
    }
}
