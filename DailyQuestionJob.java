package com.qa.nal;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

public class DailyQuestionJob implements Job {
    private static final String[][] QUESTIONS = {
            { "Two Sum", "https://leetcode.com/problems/two-sum/" },
            { "Add Two Numbers", "https://leetcode.com/problems/add-two-numbers/" },
            { "Longest Substring Without Repeating Characters",
                    "https://leetcode.com/problems/longest-substring-without-repeating-characters/" }
    };
    private static int counter = 0;
    private static final String EMAIL_USERNAME = "mohammedkhan9271@gmail.com";
    private static final String EMAIL_PASSWORD = "zhzjizblebzxolvh";

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String[] question = QUESTIONS[counter % QUESTIONS.length];
        counter++;
        String subject = "Daily DSA Question: " + question[0];
        String content = "Here is your daily DSA question:\n\n" + question[0] + "\n" + question[1];
        sendEmail("kazeemun@gmail.com", subject, content);
    }

    public static void sendEmail(String to, String subject, String content) {
        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_USERNAME, EMAIL_PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_USERNAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(content);
            Transport.send(message);
            System.out.println("Email sent successfully");
        } catch (MessagingException e) {
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            JobDetail job = JobBuilder.newJob(DailyQuestionJob.class).withIdentity("dailyQuestionJob", "group1")
                    .build();
            Trigger trigger = TriggerBuilder.newTrigger().withIdentity("dailyTrigger", "group1")
                    .withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(14, 0)).build();
            Scheduler scheduler = new StdSchedulerFactory().getScheduler();
            scheduler.start();
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            System.err.println("Scheduler error: " + e.getMessage());
        }
    }
}
