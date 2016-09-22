package com.pgs.spark.bigdata.web.rest;

import com.pgs.spark.bigdata.AnalyzerApp;
import com.pgs.spark.bigdata.domain.Authority;
import com.pgs.spark.bigdata.domain.PersistentToken;
import com.pgs.spark.bigdata.domain.User;
import com.pgs.spark.bigdata.repository.AuthorityRepository;
import com.pgs.spark.bigdata.repository.PersistentTokenRepository;
import com.pgs.spark.bigdata.repository.UserRepository;
import com.pgs.spark.bigdata.security.AuthoritiesConstants;
import com.pgs.spark.bigdata.service.MailService;
import com.pgs.spark.bigdata.service.UserService;
import com.pgs.spark.bigdata.web.rest.dto.KeyAndPasswordDTO;
import com.pgs.spark.bigdata.web.rest.dto.ManagedUserDTO;
import com.pgs.spark.bigdata.web.rest.dto.UserDTO;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for the AccountResource REST controller.
 *
 * @see UserService
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AnalyzerApp.class)
@WebAppConfiguration
@IntegrationTest
public class AccountResourceIntTest {

    @Inject
    private UserRepository userRepository;

    @Inject
    private AuthorityRepository authorityRepository;

    @Inject
    private UserService userService;

    @Inject
    private PasswordEncoder passwordEncoder;

    @Inject
    private PersistentTokenRepository persistentTokenRepository;

    @Mock
    private UserService mockUserService;

    @Mock
    private MailService mockMailService;

    @Mock
    private PersistentTokenRepository mockPersistentTokenRepository;

    private MockMvc restUserMockMvc;

    private MockMvc restMvc;

    private AccountResource accountResource;

    private AccountResource accountUserMockResource;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        doNothing().when(mockMailService).sendActivationEmail((User) anyObject(), anyString());

        accountResource = new AccountResource();
        ReflectionTestUtils.setField(accountResource, "userRepository", userRepository);
        ReflectionTestUtils.setField(accountResource, "userService", userService);
        ReflectionTestUtils.setField(accountResource, "mailService", mockMailService);
        ReflectionTestUtils.setField(accountResource, "persistentTokenRepository", persistentTokenRepository);

        accountUserMockResource = new AccountResource();
        ReflectionTestUtils.setField(accountUserMockResource, "userRepository", userRepository);
        ReflectionTestUtils.setField(accountUserMockResource, "userService", mockUserService);
        ReflectionTestUtils.setField(accountUserMockResource, "mailService", mockMailService);
        ReflectionTestUtils.setField(accountUserMockResource, "persistentTokenRepository", mockPersistentTokenRepository);

        this.restMvc = MockMvcBuilders.standaloneSetup(accountResource).build();
        this.restUserMockMvc = MockMvcBuilders.standaloneSetup(accountUserMockResource).build();
    }

    @Test
    public void testNonAuthenticatedUser() throws Exception {
        restUserMockMvc.perform(get("/api/authenticate")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(""));
    }

    @Test
    public void testAuthenticatedUser() throws Exception {
        restUserMockMvc.perform(get("/api/authenticate")
            .with(request -> {
                request.setRemoteUser("test");
                return request;
            })
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string("test"));
    }

    @Test
    public void shouldActivateUserAccount() throws Exception {
        //given
        final User testUser = User.builder()
            .login(RandomStringUtils.randomAlphabetic(10).toLowerCase())
            .password(passwordEncoder.encode(RandomStringUtils.randomAlphabetic(10)))
            .email(RandomStringUtils.randomAlphabetic(10) + "@random.com")
            .activated(false)
            .activationKey(RandomStringUtils.randomAlphabetic(20))
            .build();

        final User user = userRepository.save(testUser);

        //when
        restMvc.perform(get("/api/activate")
            .param("key", user.getActivationKey())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(""));

        //then
        final User activatedUser = userRepository.findOne(user.getId());
        assertTrue(activatedUser.getActivated());
        userRepository.delete(user);
    }

    @Test
    public void shouldSaveAccount() throws Exception {
        //given
        final String userLogin = RandomStringUtils.randomAlphabetic(10).toLowerCase();
        final UserRepository mockedUserRepository = mock(UserRepository.class);
        final User testUser = User.builder()
            .login(userLogin)
            .password(passwordEncoder.encode(RandomStringUtils.randomAlphabetic(10)))
            .email(RandomStringUtils.randomAlphabetic(10) + "@random.com")
            .activated(false)
            .activationKey(RandomStringUtils.randomAlphabetic(20))
            .build();

        when(mockedUserRepository.findOneByLogin(any())).thenReturn(Optional.of(testUser));
        when(mockedUserRepository.findOneByEmail(any())).thenReturn(Optional.of(testUser));

        ReflectionTestUtils.setField(accountUserMockResource, "userRepository", mockedUserRepository);

        final UserDTO userDTO = UserDTO.builder()
            .email(testUser.getEmail())
            .login(testUser.getLogin())
            .build();


        //when
        restUserMockMvc.perform(post("/api/account")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(userDTO)))
            //then
            .andExpect(status().isOk());

        verify(mockUserService).updateUserInformation(testUser.getFirstName(), testUser.getLastName(), testUser.getEmail(), testUser.getLangKey());
    }

    @Test
    public void shouldChangePasswordToUserAccount() throws Exception {
        //given
        final String password = RandomStringUtils.randomAlphabetic(20);

        final User testUser = User.builder()
            .login(RandomStringUtils.randomAlphabetic(15))
            .password(passwordEncoder.encode(RandomStringUtils.randomAlphabetic(10)))
            .email(RandomStringUtils.randomAlphabetic(10) + "@random.com")
            .activated(false)
            .activationKey(RandomStringUtils.randomAlphabetic(20))
            .build();

        final UserRepository mockedUserRepository = mock(UserRepository.class);
        when(mockedUserRepository.findOneByLogin(any())).thenReturn(Optional.of(testUser));

        ReflectionTestUtils.setField(accountResource, "userRepository", mockedUserRepository);
        //when
        restUserMockMvc.perform(post("/api/account/change_password")
            .accept(MediaType.TEXT_PLAIN_VALUE)
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(password)
        )
            //then
            .andExpect(status().isOk());
        verify(mockUserService).changePassword(password);

    }

    @Test
    public void shouldReturnPersistentTokens() throws Exception {
        //given
        final User testUser = User.builder()
            .login(RandomStringUtils.randomAlphabetic(15))
            .password(passwordEncoder.encode(RandomStringUtils.randomAlphabetic(10)))
            .email(RandomStringUtils.randomAlphabetic(10) + "@random.com")
            .activated(false)
            .activationKey(RandomStringUtils.randomAlphabetic(20))
            .build();

        final UserRepository mockedUserRepository = mock(UserRepository.class);
        ReflectionTestUtils.setField(accountUserMockResource, "userRepository", mockedUserRepository);

        when(mockedUserRepository.findOneByLogin(any())).thenReturn(Optional.of(testUser));

        final List<PersistentToken> tokenList = Arrays.asList(new PersistentToken(), new PersistentToken());
        when(mockedUserRepository.findOneByLogin(any())).thenReturn(Optional.of(testUser));
        when(mockPersistentTokenRepository.findByUser(any())).thenReturn(tokenList);

        //when
        restUserMockMvc.perform(get("/api/account/sessions")
            .accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isInternalServerError()
        );

        //then

    }

    @Test
    public void shouldRequestPasswordReset() throws Exception {
        //given
        final User testUser = User.builder()
            .login(RandomStringUtils.randomAlphabetic(15))
            .password(passwordEncoder.encode(RandomStringUtils.randomAlphabetic(10)))
            .email(RandomStringUtils.randomAlphabetic(10) + "@random.com")
            .activated(false)
            .activationKey(RandomStringUtils.randomAlphabetic(20))
            .build();

        final String email = RandomStringUtils.randomAlphabetic(10) + "@random.com";
        when(mockUserService.requestPasswordReset(email)).thenReturn(Optional.of(testUser));

        //when
        restUserMockMvc.perform(post("/api/account/reset_password/init")
            .contentType(MediaType.APPLICATION_JSON)
            .content(email)
        ).andExpect(status().isOk());

        //then
        verify(mockMailService).sendPasswordResetMail(eq(testUser), anyString());
    }

    @Test
    public void shouldFinishPasswordReset() throws Exception {
        //given
        final User testUser = User.builder()
            .login(RandomStringUtils.randomAlphabetic(15))
            .password(passwordEncoder.encode(RandomStringUtils.randomAlphabetic(10)))
            .email(RandomStringUtils.randomAlphabetic(10) + "@random.com")
            .activated(false)
            .activationKey(RandomStringUtils.randomAlphabetic(20))
            .build();

        final KeyAndPasswordDTO keyAndPasswordDTO = new KeyAndPasswordDTO();
        keyAndPasswordDTO.setKey(RandomStringUtils.randomAlphabetic(10));
        keyAndPasswordDTO.setNewPassword(RandomStringUtils.randomAlphabetic(10));
        when(mockUserService.completePasswordReset(keyAndPasswordDTO.getNewPassword(), keyAndPasswordDTO.getKey()))
            .thenReturn(Optional.of(testUser));

        //when
        restUserMockMvc.perform(post("/api/account/reset_password/finish")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(keyAndPasswordDTO))
        ).andExpect(status().isOk());

        verify(mockUserService).completePasswordReset(keyAndPasswordDTO.getNewPassword(), keyAndPasswordDTO.getKey());
    }

    @Test
    public void testGetExistingAccount() throws Exception {
        Set<Authority> authorities = new HashSet<>();
        Authority authority = new Authority();
        authority.setName(AuthoritiesConstants.ADMIN);
        authorities.add(authority);

        User user = new User();
        user.setLogin("test");
        user.setFirstName("john");
        user.setLastName("doe");
        user.setEmail("john.doe@jhipter.com");
        user.setAuthorities(authorities);
        when(mockUserService.getUserWithAuthorities()).thenReturn(user);

        restUserMockMvc.perform(get("/api/account")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.login").value("test"))
            .andExpect(jsonPath("$.firstName").value("john"))
            .andExpect(jsonPath("$.lastName").value("doe"))
            .andExpect(jsonPath("$.email").value("john.doe@jhipter.com"))
            .andExpect(jsonPath("$.authorities").value(AuthoritiesConstants.ADMIN));
    }

    @Test
    public void testGetUnknownAccount() throws Exception {
        when(mockUserService.getUserWithAuthorities()).thenReturn(null);

        restUserMockMvc.perform(get("/api/account")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isInternalServerError());
    }

    @Test
    @Transactional
    public void testRegisterValid() throws Exception {
        ManagedUserDTO validUser = new ManagedUserDTO(
            null,                   // id
            "joe",                  // login
            "password",             // password
            "Joe",                  // firstName
            "Shmoe",                // lastName
            "joe@example.com",      // e-mail
            true,                   // activated
            "en",               // langKey
            new HashSet<>(Arrays.asList(AuthoritiesConstants.USER)),
            null,                   // createdDate
            null,                   // lastModifiedBy
            null                    // lastModifiedDate
        );

        restMvc.perform(
            post("/api/register")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(validUser)))
            .andExpect(status().isCreated());

        Optional<User> user = userRepository.findOneByLogin("joe");
        assertThat(user.isPresent()).isTrue();
    }

    @Test
    @Transactional
    public void testRegisterInvalidLogin() throws Exception {
        ManagedUserDTO invalidUser = new ManagedUserDTO(
            null,                   // id
            "funky-log!n",          // login <-- invalid
            "password",             // password
            "Funky",                // firstName
            "One",                  // lastName
            "funky@example.com",    // e-mail
            true,                   // activated
            "en",               // langKey
            new HashSet<>(Arrays.asList(AuthoritiesConstants.USER)),
            null,                   // createdDate
            null,                   // lastModifiedBy
            null                    // lastModifiedDate
        );

        restUserMockMvc.perform(
            post("/api/register")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(invalidUser)))
            .andExpect(status().isBadRequest());

        Optional<User> user = userRepository.findOneByEmail("funky@example.com");
        assertThat(user.isPresent()).isFalse();
    }

    @Test
    @Transactional
    public void testRegisterInvalidEmail() throws Exception {
        ManagedUserDTO invalidUser = new ManagedUserDTO(
            null,                   // id
            "bob",              // login
            "password",         // password
            "Bob",              // firstName
            "Green",            // lastName
            "invalid",          // e-mail <-- invalid
            true,               // activated
            "en",               // langKey
            new HashSet<>(Arrays.asList(AuthoritiesConstants.USER)),
            null,                   // createdDate
            null,                   // lastModifiedBy
            null                    // lastModifiedDate
        );

        restUserMockMvc.perform(
            post("/api/register")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(invalidUser)))
            .andExpect(status().isBadRequest());

        Optional<User> user = userRepository.findOneByLogin("bob");
        assertThat(user.isPresent()).isFalse();
    }

    @Test
    @Transactional
    public void testRegisterInvalidPassword() throws Exception {
        ManagedUserDTO invalidUser = new ManagedUserDTO(
            null,                   // id
            "bob",              // login
            "123",              // password with only 3 digits
            "Bob",              // firstName
            "Green",            // lastName
            "bob@example.com",  // e-mail
            true,               // activated
            "en",               // langKey
            new HashSet<>(Arrays.asList(AuthoritiesConstants.USER)),
            null,                   // createdDate
            null,                   // lastModifiedBy
            null                    // lastModifiedDate
        );

        restUserMockMvc.perform(
            post("/api/register")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(invalidUser)))
            .andExpect(status().isBadRequest());

        Optional<User> user = userRepository.findOneByLogin("bob");
        assertThat(user.isPresent()).isFalse();
    }

    @Test
    @Transactional
    public void testRegisterEmailEmpty() throws Exception {
        ManagedUserDTO invalidUser = new ManagedUserDTO(
            null,                   // id
            "bob",              // login
            "password",         // password
            "Bob",              // firstName
            "Green",            // lastName
            "",                 // e-mail <-- empty
            true,               // activated
            "en",               // langKey
            new HashSet<>(Arrays.asList(AuthoritiesConstants.USER)),
            null,                   // createdDate
            null,                   // lastModifiedBy
            null                    // lastModifiedDate
        );

        restUserMockMvc.perform(
            post("/api/register")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(invalidUser)))
            .andExpect(status().isBadRequest());

        Optional<User> user = userRepository.findOneByLogin("bob");
        assertThat(user.isPresent()).isFalse();
    }

    @Test
    @Transactional
    public void testRegisterDuplicateLogin() throws Exception {
        // Good
        ManagedUserDTO validUser = new ManagedUserDTO(
            null,                   // id
            "alice",                // login
            "password",             // password
            "Alice",                // firstName
            "Something",            // lastName
            "alice@example.com",    // e-mail
            true,                   // activated
            "en",               // langKey
            new HashSet<>(Arrays.asList(AuthoritiesConstants.USER)),
            null,                   // createdDate
            null,                   // lastModifiedBy
            null                    // lastModifiedDate
        );

        // Duplicate login, different e-mail
        ManagedUserDTO duplicatedUser = new ManagedUserDTO(validUser.getId(), validUser.getLogin(), validUser.getPassword(), validUser.getLogin(), validUser.getLastName(),
            "alicejr@example.com", true, validUser.getLangKey(), validUser.getAuthorities(), validUser.getCreatedDate(), validUser.getLastModifiedBy(), validUser.getLastModifiedDate());

        // Good user
        restMvc.perform(
            post("/api/register")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(validUser)))
            .andExpect(status().isCreated());

        // Duplicate login
        restMvc.perform(
            post("/api/register")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(duplicatedUser)))
            .andExpect(status().is4xxClientError());

        Optional<User> userDup = userRepository.findOneByEmail("alicejr@example.com");
        assertThat(userDup.isPresent()).isFalse();
    }

    @Test
    @Transactional
    public void testRegisterDuplicateEmail() throws Exception {
        // Good
        ManagedUserDTO validUser = new ManagedUserDTO(
            null,                   // id
            "john",                 // login
            "password",             // password
            "John",                 // firstName
            "Doe",                  // lastName
            "john@example.com",     // e-mail
            true,                   // activated
            "en",               // langKey
            new HashSet<>(Arrays.asList(AuthoritiesConstants.USER)),
            null,                   // createdDate
            null,                   // lastModifiedBy
            null                    // lastModifiedDate
        );

        // Duplicate e-mail, different login
        ManagedUserDTO duplicatedUser = new ManagedUserDTO(validUser.getId(), "johnjr", validUser.getPassword(), validUser.getLogin(), validUser.getLastName(),
            validUser.getEmail(), true, validUser.getLangKey(), validUser.getAuthorities(), validUser.getCreatedDate(), validUser.getLastModifiedBy(), validUser.getLastModifiedDate());

        // Good user
        restMvc.perform(
            post("/api/register")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(validUser)))
            .andExpect(status().isCreated());

        // Duplicate e-mail
        restMvc.perform(
            post("/api/register")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(duplicatedUser)))
            .andExpect(status().is4xxClientError());

        Optional<User> userDup = userRepository.findOneByLogin("johnjr");
        assertThat(userDup.isPresent()).isFalse();
    }

    @Test
    @Transactional
    public void testRegisterAdminIsIgnored() throws Exception {
        ManagedUserDTO validUser = new ManagedUserDTO(
            null,                   // id
            "badguy",               // login
            "password",             // password
            "Bad",                  // firstName
            "Guy",                  // lastName
            "badguy@example.com",   // e-mail
            true,                   // activated
            "en",               // langKey
            new HashSet<>(Arrays.asList(AuthoritiesConstants.ADMIN)),
            null,                   // createdDate
            null,                   // lastModifiedBy
            null                    // lastModifiedDate
        );

        restMvc.perform(
            post("/api/register")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(validUser)))
            .andExpect(status().isCreated());

        Optional<User> userDup = userRepository.findOneByLogin("badguy");
        assertThat(userDup.isPresent()).isTrue();
        assertThat(userDup.get().getAuthorities()).hasSize(1)
            .containsExactly(authorityRepository.findOne(AuthoritiesConstants.USER));
    }

    @Test
    @Transactional
    public void testSaveInvalidLogin() throws Exception {
        UserDTO invalidUser = new UserDTO(
            "funky-log!n",          // login <-- invalid
            "Funky",                // firstName
            "One",                  // lastName
            "funky@example.com",    // e-mail
            true,                   // activated
            "en",               // langKey
            new HashSet<>(Arrays.asList(AuthoritiesConstants.USER))
        );

        restUserMockMvc.perform(
            post("/api/account")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(invalidUser)))
            .andExpect(status().isBadRequest());

        Optional<User> user = userRepository.findOneByEmail("funky@example.com");
        assertThat(user.isPresent()).isFalse();
    }
}
