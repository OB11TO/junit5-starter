package com.ob11to.junit.extension;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class GlobalExtensionCallback implements BeforeAllCallback, AfterAllCallback {

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        System.out.println("before all callback");
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        System.out.println("after all callback");
    }
}
