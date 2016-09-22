package com.pgs.spark.bigdata.service;

import com.pgs.spark.bigdata.config.JHipsterProperties;
import com.pgs.spark.bigdata.domain.User;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Collections;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SpringTemplateEngine.class)
public class MailServiceTest {

    @InjectMocks
    private MailService mailService = new MailService();

    @Mock
    private JHipsterProperties jHipsterProperties;

    @Mock
    private JavaMailSenderImpl javaMailSender;

    private MessageSource messageSource;

    private SpringTemplateEngine templateEngine;

    @Before
    public void setUp(){
        templateEngine = PowerMockito.mock(SpringTemplateEngine.class);
        messageSource = PowerMockito.mock(MessageSource.class);
        PowerMockito.when(messageSource.getMessage(anyString(), any(), any(Locale.class)))
            .thenReturn("exampleContent");
        when(javaMailSender.createMimeMessage()).thenCallRealMethod();
        when(jHipsterProperties.getMail()).thenReturn(new JHipsterProperties.Mail());

        ReflectionTestUtils.setField(mailService, "templateEngine", templateEngine);
        ReflectionTestUtils.setField(mailService, "messageSource", messageSource);
    }

    @Test
    public void shouldSendEmail() throws MessagingException, IOException {
        //given
        final String to = RandomStringUtils.randomAlphabetic(10);
        final String subject = RandomStringUtils.randomAlphabetic(10);
        final String content = RandomStringUtils.randomAlphabetic(10);
        final boolean isMultipart = false;
        final boolean isHtml = true;
        final ArgumentCaptor<MimeMessage> messageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
        when(javaMailSender.createMimeMessage()).thenCallRealMethod();

        //when
        mailService.sendEmail(to, subject, content, isMultipart, isHtml);

        //then
        verify(javaMailSender).send(messageCaptor.capture());
        final MimeMessage message = messageCaptor.getValue();
        assertEquals(to, message.getAllRecipients()[0].toString());
        assertEquals(subject, message.getSubject());
        assertEquals(content, message.getContent().toString());
    }

    @Test
    public void shouldSendActivationEmail() throws IOException, MessagingException {
        //given
        final User user = getExampleUser();
        final String baseUrl = RandomStringUtils.randomAlphabetic(10);
        final ArgumentCaptor<MimeMessage> messageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
        final String content = RandomStringUtils.randomAlphabetic(10);
        PowerMockito.when(templateEngine.process(anyString(), any(Context.class))).thenReturn(content);
        PowerMockito.when(templateEngine.getDialects()).thenReturn(Collections.emptySet());
        PowerMockito.when(templateEngine.process(anyString(), any())).thenReturn(content);

        //when
        mailService.sendActivationEmail(user, baseUrl);

        //then
        verify(javaMailSender).send(messageCaptor.capture());
        final MimeMessage message = messageCaptor.getValue();
        assertTrue(message.getAllRecipients().length == 1);
        assertEquals(user.getEmail(), message.getAllRecipients()[0].toString());
        assertEquals(content, message.getContent());
    }

    @Test
    public void shouldSendCreationEmail() throws MessagingException, IOException {
        //given
        final User user = getExampleUser();
        final String baseUrl = RandomStringUtils.randomAlphabetic(10);
        final ArgumentCaptor<MimeMessage> messageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
        final String content = RandomStringUtils.randomAlphabetic(10);
        PowerMockito.when(templateEngine.getDialects()).thenReturn(Collections.emptySet());
        PowerMockito.when(templateEngine.process(any(), any())).thenReturn(content);

        //when
        mailService.sendCreationEmail(user, baseUrl);

        //then
        verify(javaMailSender).send(messageCaptor.capture());
        final MimeMessage message = messageCaptor.getValue();
        assertTrue(message.getAllRecipients().length == 1);
        assertEquals(user.getEmail(), message.getAllRecipients()[0].toString());
        assertEquals(content, message.getContent());
    }

    @Test
    public void shouldSendPasswordResetMail() throws IOException, MessagingException {
        //given
        final User user = getExampleUser();
        final String baseUrl = RandomStringUtils.randomAlphabetic(10);
        final ArgumentCaptor<MimeMessage> messageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
        final String content = RandomStringUtils.randomAlphabetic(10);
        PowerMockito.when(templateEngine.getDialects()).thenReturn(Collections.emptySet());
        PowerMockito.when(templateEngine.process(any(), any())).thenReturn(content);

        //when
        mailService.sendPasswordResetMail(user, baseUrl);

        //then
        verify(javaMailSender).send(messageCaptor.capture());
        final MimeMessage message = messageCaptor.getValue();
        assertTrue(message.getAllRecipients().length == 1);
        assertEquals(user.getEmail(), message.getAllRecipients()[0].toString());
        assertEquals(content, message.getContent());
    }

    private User getExampleUser() {
        final User user = new User();
        user.setFirstName(RandomStringUtils.randomAlphabetic(10));
        user.setLastName(RandomStringUtils.randomAlphabetic(10));
        user.setLangKey(RandomStringUtils.randomAlphabetic(10));
        user.setActivationKey(RandomStringUtils.randomAlphabetic(10));
        user.setLogin(RandomStringUtils.randomAlphabetic(10));
        user.setPassword(RandomStringUtils.randomAlphabetic(10));
        user.setEmail(RandomStringUtils.randomAlphabetic(4) + "@random.com");
        return user;
    }
}
