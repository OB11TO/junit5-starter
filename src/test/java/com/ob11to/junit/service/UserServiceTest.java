package com.ob11to.junit.service;

import com.ob11to.junit.dto.User;
import org.junit.jupiter.api.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@TestInstance(value = TestInstance.Lifecycle.PER_METHOD) //по умолчанию (каждый раз создается новый объект класса)
class UserServiceTest {

    //Глобальные переменные
    private UserService userService;
    private static final User IVAN = User.of(1, "Ivan", "123");
    private static final User PETR = User.of(2, "Petr", "321");

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
        userService.add(IVAN);
        userService.add(PETR);
        var users = userService.getAll();
        assertEquals(2, users.size());
    }

    @Test
    void loginSuccessIfUserExists(){
        userService.add(IVAN);
        Optional<User> maybeUser = userService.login(IVAN.getUsername(), IVAN.getPassword());

        assertTrue(maybeUser.isPresent());
        maybeUser.ifPresent(user -> assertEquals(user,IVAN));
    }

    @Test
    void loginFailIfPasswordIsNotCurrent(){
        userService.add(IVAN);
        var maybeUser = userService.login(IVAN.getUsername(), "dummy");

        assertTrue(maybeUser.isEmpty());
    }

    @Test
    void loginFailIfUserDoesNotExist(){
        userService.add(IVAN);
        var maybeUser = userService.login("dummy", "dummy");

        assertTrue(maybeUser.isEmpty());
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
