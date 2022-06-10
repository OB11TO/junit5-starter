package com.ob11to.junit.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class UserServiceTest {

    @Test
    void usersEmptyIfNoUserAdded(){
        var userService = new UserService();
        var users = userService.getAll();
        assertTrue(users.isEmpty());
    }
}
