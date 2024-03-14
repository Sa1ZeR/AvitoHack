package ru.avito.priceservice.annotation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
public class PostInitCacheInvokerContextListener implements ApplicationListener<ContextRefreshedEvent> {
    @Autowired
    private ConfigurableListableBeanFactory factory;
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        var context = event.getApplicationContext();
        var names = context.getBeanDefinitionNames();
        for (String name : names) {
            var beanDefinition = factory.getBeanDefinition(name);
            var originalClassName = beanDefinition.getBeanClassName();
            if (originalClassName == null) {
                continue;
            }
            try {
                var originalClass = Class.forName(originalClassName);
                var methods = originalClass.getMethods();
                for (Method method : methods) {
                    if (method.isAnnotationPresent(PostInitCache.class)) {
                        var bean = context.getBean(name);
                        var currentMethod = bean.getClass().getMethod(method.getName(), method.getParameterTypes());
                        currentMethod.invoke(bean);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
