package com.ob11to.junit.service;

import com.ob11to.junit.dao.UserDao;
import com.ob11to.junit.dto.User;
import com.ob11to.junit.extension.GlobalExtensionCallback;
import com.ob11to.junit.extension.PostProcessingExtension;
import com.ob11to.junit.extension.UserServiceParamResolver;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.mockito.Mockito;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.TestInstance.*;

@Tag("fast")
@TestInstance(value = Lifecycle.PER_METHOD) //по умолчанию (каждый раз создается новый объект класса)
@ExtendWith({
        UserServiceParamResolver.class,
        GlobalExtensionCallback.class,
        PostProcessingExtension.class
})
class UserServiceTest {

    //Глобальные переменные
    private UserService userService;
    private UserDao userDao;
    private static final User IVAN = User.of(1, "Ivan", "123");
    private static final User PETR = User.of(2, "Petr", "321");

    @BeforeAll
    static void init() {
        System.out.println("Before all");
    }

    @BeforeEach
    void prepare() {
        System.out.println("Before each " + this);
        //инициализация переменных
//        userDao = new UserDao(); так нельзя делать, так как не нужно реально обращаться к методу
//        userDao = Mockito.mock(UserDao.class); // делаем мок, теперь нужно запрограммировать
        userDao = Mockito.spy(new UserDao());
        userService = new UserService(userDao);
    }

    @Test
    void shouldDeleteExistedUser() {
        userService.add(IVAN);

        //Первый вариант
        Mockito.doReturn(true).when(userDao).delete(IVAN.getId());
//        Mockito.doReturn(true).when(userDao).delete(Mockito.any());  разница в том, что без разницы какой объект удалили

        //Второй вариант
//        Mockito.when(userDao.delete(IVAN.getId()))
//                .thenReturn(true)
//                .thenReturn(false); //1 раз удаляет, все остальные будут false

        var deleteResult = userService.delete(IVAN.getId());

//        assertTrue(deleteResult);
        assertThat(deleteResult).isTrue();

        System.out.println(userService.delete(IVAN.getId()));
        System.out.println(userService.delete(IVAN.getId()));

        Mockito.verify(userDao, Mockito.times(3)).delete(IVAN.getId());
    }


    @Test
    void usersEmptyIfNoUserAdded() {
        System.out.println("Test 1: " + this);
        var users = userService.getAll();

        assertThat(users).isEmpty();
//        assertTrue(users.isEmpty(), "NO Empty");
    }

    @Test
    void usersSizeIfUserAdded() {
        System.out.println("Test 2: " + this);
        userService.add(IVAN);
        userService.add(PETR);
        var users = userService.getAll();

        assertThat(users).hasSize(2);
//        assertEquals(2, users.size());
    }


    @Test
    void userConvertToMapById() {
        userService.add(IVAN, PETR);
        Map<Integer, User> userMap = userService.getAllConvertedById();

        assertAll(
                () -> assertThat(userMap).containsKeys(IVAN.getId(), PETR.getId()),
                () -> assertThat(userMap).containsValues(IVAN, PETR)
        );
    }


    @AfterEach
    void deleteDataFromDatabase() {
        System.out.println("After each " + this);
    }

    @AfterAll
    static void closeConnectionPool() {
        System.out.println("After all ");
    }


    @Nested
    @Tag("login")
    @DisplayName("User logging test")
    class LoginTest {

        @Test
        void loginSuccessIfUserExists() {
            userService.add(IVAN);
            Optional<User> maybeUser = userService.login(IVAN.getUsername(), IVAN.getPassword());

            assertThat(maybeUser).isPresent();
            maybeUser.ifPresent(user -> assertThat(user).isEqualTo(IVAN));
//        assertTrue(maybeUser.isPresent());
//        maybeUser.ifPresent(user -> assertEquals(user, IVAN));
        }

        @Test
        @Disabled("flaky test")
            //отдельно тест запускается, а так нет
        void loginFailIfPasswordIsNotCurrent() {
            userService.add(IVAN);
            var maybeUser = userService.login(IVAN.getUsername(), "dummy");

            assertTrue(maybeUser.isEmpty());
        }

        @RepeatedTest(value = 5, name = RepeatedTest.LONG_DISPLAY_NAME)
        void loginFailIfUserDoesNotExist() {
            userService.add(IVAN);
            var maybeUser = userService.login("dummy", "dummy");

            assertTrue(maybeUser.isEmpty());
        }

        //Timout
        @Test
        @Timeout(value = 100L, unit = TimeUnit.MILLISECONDS)
        //можно писать над классами, лучше использовать их не для юнит тестов
        void checkLoginFunctionalityPerformance() {
            //выполниться в отдельном потоке
            var result = assertTimeoutPreemptively(Duration.ofMillis(200L), () -> userService.login("dummy", "dummy"));

        }

        @Test
//    @org.junit.Test(expected = IllegalArgumentException.class)
        void throwExceptionIfUsernameOrPasswordByNull() {
            assertAll(
                    () -> {
                        var exception = assertThrows(IllegalArgumentException.class, () -> userService.login(null, "dummy"));
                        assertThat(exception.getMessage()).isEqualTo("username or password null");
                    },
                    () -> assertThrows(IllegalArgumentException.class, () -> userService.login("dummy", null))
            );
//        Так делать не правильно
//        try {
//            userService.login(null, "dummy");
//            fail();
//        } catch (IllegalArgumentException exception) {
//            assertTrue(true);
//        }
        }

        @ParameterizedTest
//        @ParameterizedTest(name = "{arguments} test")
//        @ArgumentsSource()
//        @NullSource
//        @EmptySource
//        @NullAndEmptySource
//        @ValueSource(strings = {
//                "Ivan", "Petr"
//        })
        @MethodSource("com.ob11to.junit.service.UserServiceTest#getArgumentParameter")
        @DisplayName("login param test")
        void loginParameterTest(String username, String password, Optional<User> user) {
            userService.add(IVAN, PETR);
            var maybeUser = userService.login(username, password);
            assertThat(maybeUser).isEqualTo(user);

        }
    }

    static Stream<Arguments> getArgumentParameter() {
        return Stream.of(
                Arguments.of("Ivan", "123", Optional.of(IVAN)),
                Arguments.of("Petr", "321", Optional.of(PETR)),
                Arguments.of("Petr", "dummy", Optional.empty()),
                Arguments.of("dummy", "123", Optional.empty())
        );

    }
}
