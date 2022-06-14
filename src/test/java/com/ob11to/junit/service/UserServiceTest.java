package com.ob11to.junit.service;

import com.ob11to.junit.dto.User;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@TestInstance(value = TestInstance.Lifecycle.PER_METHOD) //по умолчанию (каждый раз создается новый объект класса)
class UserServiceTest {

    //Глобальные переменные
    private UserService userService;

    @BeforeAll
    static void init(){
        System.out.println("Before all");
    }

    @BeforeEach
    void prepare() {
        System.out.println("Before each " + this);
        //инициализация переменных
        userService = new UserService();
    }


    @Test
    void usersEmptyIfNoUserAdded() {
        System.out.println("Test 1: " + this );
        var users = userService.getAll();
        assertTrue(users.isEmpty(), "NO Empty");
    }

    @Test
    void usersSizeIfUserAdded() {
        System.out.println("Test 2: " + this );
        userService.add(new User());
        userService.add(new User());
        var users = userService.getAll();
        assertEquals(2, users.size());
    }

    @AfterEach
    void deleteDataFromDatabase() {
        System.out.println("After each " + this);
    }

    @AfterAll
    static void closeConnectionPool() {
        System.out.println("After all ");
    }
}
