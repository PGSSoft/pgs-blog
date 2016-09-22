package com.pgs.spark.bigdata.web.rest;

import com.pgs.spark.bigdata.AnalyzerApp;
import com.pgs.spark.bigdata.domain.Authority;
import com.pgs.spark.bigdata.domain.User;
import com.pgs.spark.bigdata.repository.UserRepository;
import com.pgs.spark.bigdata.service.MailService;
import com.pgs.spark.bigdata.service.UserService;
import com.pgs.spark.bigdata.web.rest.dto.ManagedUserDTO;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for the UserResource REST controller.
 *
 * @see UserResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AnalyzerApp.class)
@WebAppConfiguration
@IntegrationTest
public class UserResourceIntTest {

    @Inject
    private UserRepository userRepository;

    @Inject
    private UserService userService;

    @Inject
    private PasswordEncoder passwordEncoder;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Mock
    private MailService mailService;

    private MockMvc restUserMockMvc;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        UserResource userResource = new UserResource();
        ReflectionTestUtils.setField(userResource, "userRepository", userRepository);
        ReflectionTestUtils.setField(userResource, "userService", userService);
        ReflectionTestUtils.setField(userResource, "mailService", mailService);
        this.restUserMockMvc = MockMvcBuilders
            .standaloneSetup(userResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .build();
    }

    @Test
    public void testGetExistingUser() throws Exception {
        restUserMockMvc.perform(get("/api/users/admin")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.lastName").value("Administrator"));
    }

    @Test
    public void testGetUnknownUser() throws Exception {
        restUserMockMvc.perform(get("/api/users/unknown")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    public void shouldCreateUser() throws Exception {
        //given
        final ManagedUserDTO userDTO = ManagedUserDTO.builder()
            .email(RandomStringUtils.randomAlphabetic(10) + "@random.com")
            .login(RandomStringUtils.randomAlphabetic(10).toLowerCase())
            .activated(false)
            .langKey(RandomStringUtils.randomAlphabetic(2))
            .build();

        //when
        restUserMockMvc.perform(post("/api/users")
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(userDTO))
        ).andExpect(status().isCreated());

        //then
        verify(mailService).sendCreationEmail(any(User.class), anyString());
        final Optional<User> user = userRepository.findOneByLogin(userDTO.getLogin());
        assertTrue(user.isPresent());

        //cleanUp
        userRepository.delete(user.get());
    }

    @Test
    @Transactional
    public void shouldUpdateUser() throws Exception {
        //given
        final User testUser = User.builder()
            .activationKey(RandomStringUtils.randomAlphabetic(10))
            .activated(true)
            .email(RandomStringUtils.randomAlphabetic(10) + "@random.com")
            .firstName(RandomStringUtils.randomAlphabetic(10))
            .lastName(RandomStringUtils.randomAlphabetic(10))
            .login(RandomStringUtils.randomAlphabetic(10).toLowerCase())
            .langKey(RandomStringUtils.randomAlphabetic(4))
            .authorities(Collections.emptySet())
            .password(passwordEncoder.encode(RandomStringUtils.randomAlphabetic(10)))
            .build();

        final User user = userRepository.save(testUser);

        final ManagedUserDTO userDTO = ManagedUserDTO.builder()
            .id(user.getId())
            .email(RandomStringUtils.randomAlphabetic(10) + "@random.com")
            .login(RandomStringUtils.randomAlphabetic(10).toLowerCase())
            .activated(false)
            .langKey(RandomStringUtils.randomAlphabetic(2))
            .authorities(testUser.getAuthorities().stream().map(Authority::toString).collect(Collectors.toSet()))
            .firstName(testUser.getFirstName())
            .lastName(testUser.getLastName())
            .build();

        //when
        restUserMockMvc.perform(put("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(userDTO))
        )
            //then
            .andExpect(status().isOk());
        userRepository.delete(user);
    }

    @Test
    @Transactional
    public void shouldReturnPageOfUsers() throws Exception {
        //given
        final Pageable pageable = new PageRequest(1, 3);

        //when
        restUserMockMvc.perform(get("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(pageable))
        )
            //then
            .andExpect(status().isOk());

    }

}
