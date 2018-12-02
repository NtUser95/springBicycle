package org.springframework.beans.factory;

import org.springframework.beans.factory.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class BeanFactory {
    private Map<String, Object> singletons = new HashMap();

    public void instantiate(String basePackage) {
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
                    try {
                        Class classObject = Class.forName(basePackage + "." + className);
                        if(classObject.isAnnotationPresent(Component.class)){
                            Object instance = classObject.newInstance();
                            String beanName = className.substring(0, 1).toLowerCase() + className.substring(1);
                            singletons.put(beanName, instance);
                        }
                    } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public Object getBean(String beanName){
        return singletons.get(beanName);
    }
}
