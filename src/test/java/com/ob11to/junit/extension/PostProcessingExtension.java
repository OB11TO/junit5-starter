package com.ob11to.junit.extension;

import com.ob11to.junit.dao.UserDao;
import com.ob11to.junit.service.UserService;
import lombok.Getter;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

import java.lang.reflect.Field;

public class PostProcessingExtension implements TestInstancePostProcessor {

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) throws Exception {
        System.out.println("post processing");

        var declaredFields = testInstance.getClass().getDeclaredFields();
        for (Field declaredField : declaredFields){
            if(declaredField.isAnnotationPresent(Getter.class)){
                declaredField.set(testInstance, new UserService(new UserDao()));
            }
        }
    }
}
