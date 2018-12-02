package com.gmail.borlandlp.examplewebapp;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.stereotype.Component;

@Component
public class ProductService {
    @Autowired
    private PromotionsService promotionsService;
    private BeanFactory beanFactory;

    public PromotionsService getPromotionsService() {
        return promotionsService;
    }

    public void setPromotionsService(PromotionsService promotionsService) {
        this.promotionsService = promotionsService;
    }
}
