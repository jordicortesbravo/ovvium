package com.ovvium.integration.config.schedulers;

import com.ovvium.integration.AbstractIntegrationTest;
import com.ovvium.integration.config.ServiceTestConfig;
import com.ovvium.services.app.config.schedulers.ScheduledTasks;
import com.ovvium.services.util.mail.Mail;
import com.ovvium.services.util.mail.MailHelper;
import com.ovvium.services.util.util.basic.Utils;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@Sql(scripts = "/init_data.sql", executionPhase = BEFORE_TEST_METHOD)
@ContextConfiguration(classes = {ServiceTestConfig.class, ScheduledTasks.class})
public class ScheduledTasksIT extends AbstractIntegrationTest {

    @Autowired
    private ScheduledTasks scheduledTasks;

    @MockBean
    private MailHelper mailHelper;

    @Test
    public void given_registered_user_not_verified_when_send_verify_emails_scheduled_task_then_should_send_emails() {
        scheduledTasks.sendVerifyReminderEmails();

        await().untilAsserted(() -> {
            final ArgumentCaptor<Mail> captor = ArgumentCaptor.forClass(Mail.class);
            verify(mailHelper, times(1)).sendMail(captor.capture());

            assertThat(Utils.first(captor.getValue().getRecipients()).getValue()).isEqualTo("asole@ovvium.com");
        });
    }
}