package org.springframework.beans.factory;

import org.springframework.beans.factory.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.PreDestroy;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

public class BeanFactory {
    private Map<String, Object> singletons = new HashMap();
    private List<BeanPostProcessor> postProcessors = new ArrayList<>();

    public Map<String, Object> getSingletons() {
        return singletons;
    }

    public void addPostProcessor(BeanPostProcessor postProcessor){
        postProcessors.add(postProcessor);
    }

    public void populateProperties(){
        System.out.println("==populateProperties==");

        for (Object object : singletons.values()) {
            for (Field field : object.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(Autowired.class)) {
                    for (Object dependency : singletons.values()) {
                        if (dependency.getClass().equals(field.getType())) {
                            String setterName = "set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);//setPromotionsService
                            System.out.println("Setter name = " + setterName);
                            Method setter = null;
                            try {
                                setter = object.getClass().getMethod(setterName, dependency.getClass());
                            } catch (NoSuchMethodException e) {
                                e.printStackTrace();
                            }
                            try {
                                setter.invoke(object, dependency);
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }

    public void injectBeanNames(){
        for (String name : singletons.keySet()) {
            Object bean = singletons.get(name);
            if(bean instanceof BeanNameAware){
                ((BeanNameAware) bean).setBeanName(name);
            }
        }
    }

    public void initializeBeans(){
        for (String name : singletons.keySet()) {
            Object bean = singletons.get(name);
            //post construct
            for (Method method : bean.getClass().getDeclaredMethods()) {
                if(method.isAnnotationPresent(PostConstruct.class)) {
                    System.out.println("Found PostConstruct method = " + method.getName() + " for class " + bean.getClass().getName());
                    try {
                        method.invoke(bean);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }

            for (BeanPostProcessor postProcessor : postProcessors) {
                postProcessor.postProcessBeforeInitialization(bean, name);
            }
            if (bean instanceof InitializingBean) {
                ((InitializingBean) bean).afterPropertiesSet();
            }
            for (BeanPostProcessor postProcessor : postProcessors) {
                postProcessor.postProcessAfterInitialization(bean, name);
            }
        }
    }

    public void instantiate(String basePackage) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        String path = basePackage.replace('.', '/'); //"com.gmail" -> "com/gmail"
        Enumeration<URL> resources = null;
        try {
            resources = classLoader.getResources(path);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            File file = null;
            try {
                file = new File(resource.toURI());
            } catch (URISyntaxException e) {
                e.printStackTrace();
                continue;
            }
            for(File classFile : file.listFiles()){
                String fileName = classFile.getName();//ProductService.class
                if(fileName.endsWith(".class")){
                    String className = fileName.substring(0, fileName.lastIndexOf("."));
                    Class classObject = Class.forName(basePackage + "." + className);
                    if(classObject.isAnnotationPresent(Component.class)){
                        Object instance = classObject.newInstance();
                        String beanName = className.substring(0, 1).toLowerCase() + className.substring(1);
                        singletons.put(beanName, instance);
                    }
                }
            }
        }
    }

    public Object getBean(String beanName){
        return singletons.get(beanName);
    }

    public void close() {
        for (Object bean : singletons.values()) {
            for (Method method : bean.getClass().getMethods()) {
                if (method.isAnnotationPresent(PreDestroy.class)) {
                    try {
                        method.invoke(bean);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (bean instanceof DisposableBean) {
                ((DisposableBean) bean).destroy();
            }
        }
    }
}
