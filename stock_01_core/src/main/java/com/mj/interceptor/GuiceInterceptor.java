package com.mj.interceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import com.google.inject.Inject;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.mj.plugin.GuicePlugin;
/**
 * Guice ioc interceptor
 * @author xwalker <br/> http://my.oschina.net/imhoodoo
 *
 */
public class GuiceInterceptor implements Interceptor {
    
    @Override
    public void intercept(Invocation ai) {
        /*
         * 得到拦截的controller 判断是否有依赖注入的属性
         */
        Controller controller = ai.getController();
        Field[] fields = controller.getClass().getDeclaredFields();
        for (Field field : fields) {
            Object bean = null;
            if (field.isAnnotationPresent((Class<? extends Annotation>) Inject.class))
                bean = GuicePlugin.getInstance(field.getType());
            else
                continue;
             
            try {
                if (bean != null) {
                    field.setAccessible(true);
                    field.set(controller, bean);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        ai.invoke();
 
    }
 
}