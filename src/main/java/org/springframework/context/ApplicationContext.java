package org.springframework.context;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.event.ContextClosedEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ApplicationContext {
    private BeanFactory beanFactory = new BeanFactory();

    public ApplicationContext(String basePackage) throws ReflectiveOperationException {
        System.out.println("******Context is under construction******");

        beanFactory.instantiate(basePackage);
        beanFactory.populateProperties();
        beanFactory.injectBeanNames();
        beanFactory.initializeBeans();
    }

    public void close(){
        beanFactory.close();
        for(Object bean : beanFactory.getSingletons().values()) {
            for (Type type: bean.getClass().getGenericInterfaces()){
                if(type instanceof ParameterizedType){
                    ParameterizedType parameterizedType = (ParameterizedType) type;
                    Type firstParameter = parameterizedType.getActualTypeArguments()[0];
                    if(firstParameter.equals(ContextClosedEvent.class)){
                        try {
                            Method method = bean.getClass().getMethod("onApplicationEvent", ContextClosedEvent.class);
                            method.invoke(bean, new ContextClosedEvent());
                        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}
