package com.pgs.spark.bigdata.service;

import com.pgs.spark.bigdata.config.JHipsterProperties;
import com.pgs.spark.bigdata.domain.User;
import com.pgs.spark.bigdata.repository.UserRepository;
import com.pgs.spark.bigdata.security.SecurityUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SecurityUtils.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService = new UserService();

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private User testUser;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(userService, "userRepository", userRepository);
        testUser = User.builder()
            .login(RandomStringUtils.randomAlphabetic(10).toLowerCase())
            .firstName(RandomStringUtils.randomAlphabetic(10))
            .lastName(RandomStringUtils.randomAlphabetic(10))
            .email(RandomStringUtils.randomAlphabetic(10) + "@random.com")
            .build();

        when(userRepository.findAll(any(Pageable.class)))
            .thenReturn(new PageImpl<>(Collections.singletonList(testUser)));

    }

    @Test
    public void shouldUpdateUserInformation(){
        //given
        PowerMockito.mockStatic(SecurityUtils.class);
        final Page<User> usersPage = userRepository.findAll(new PageRequest(0, 1));
        final Optional<User> optionalUser = usersPage.getContent().size() > 0 ? Optional.of(usersPage.getContent().get(0)) : Optional.empty();
        final String randomFirstName = RandomStringUtils.randomAlphabetic(10);
        final String randomLastName = RandomStringUtils.randomAlphabetic(10);
        final String randomEmail = RandomStringUtils.randomAlphabetic(10) + "@random.com";

        optionalUser.ifPresent(user -> {
            PowerMockito.when(SecurityUtils.getCurrentUserLogin()).thenReturn(user.getLogin());
            when(userRepository.findOneByLogin(user.getLogin())).thenReturn(Optional.of(user));

            //when
            userService.updateUserInformation(
                randomFirstName,
                randomLastName,
                randomEmail,
                user.getLangKey()
            );
        });

        //then
        final ArgumentCaptor<User> argumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(argumentCaptor.capture());

        assertEquals(randomFirstName, argumentCaptor.getValue().getFirstName());
        assertEquals(randomLastName, argumentCaptor.getValue().getLastName());
        assertEquals(randomEmail, argumentCaptor.getValue().getEmail());
    }

    @Test
    public void shouldDeleteUserInformation(){
        //given
        when(userRepository.findOneByLogin(testUser.getLogin())).thenReturn(Optional.of(testUser));

        //when
        userService.deleteUserInformation(testUser.getLogin());

        //then
        verify(userRepository).delete(testUser);
    }

    @Test
    public void shouldChangeUserPassword(){
        //given
        PowerMockito.mockStatic(SecurityUtils.class);
        PowerMockito.when(SecurityUtils.getCurrentUserLogin()).thenReturn(testUser.getLogin());
        when(userRepository.findOneByLogin(testUser.getLogin())).thenReturn(Optional.of(testUser));
        final String newPassword = RandomStringUtils.randomAlphabetic(10);
        final String encryptedPassword = RandomStringUtils.randomAlphabetic(60);
        when(passwordEncoder.encode(newPassword)).thenReturn(encryptedPassword);

        //when
        userService.changePassword(newPassword);

        //then
        final ArgumentCaptor<User> argumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(argumentCaptor.capture());

        final User user = argumentCaptor.getValue();
        assertEquals(encryptedPassword, user.getPassword());
    }

}
