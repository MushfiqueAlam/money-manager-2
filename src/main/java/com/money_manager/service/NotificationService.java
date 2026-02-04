package com.money_manager.service;

import com.money_manager.dto.ExpenseDto;
import com.money_manager.entity.ProfileEntity;
import com.money_manager.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final ProfileRepository profileRepository;
    private final EmailService emailService;
    private final ExpenseService expenseService;

    @Value("${MONEY_MANAGER_FRONTEND_URL}")
    private String frontendUrl;

    @Scheduled(cron = "0 0 22 * * *",zone = "IST")
//@Scheduled(cron = "0 * * * * *", zone = "IST") // for testing
public void sendDailyIncomeExpenseReminder() {

    log.info("Job started: sendDailyIncomeExpenseReminder()");

    List<ProfileEntity> profiles = profileRepository.findAll();

    for (ProfileEntity profile : profiles) {

        String body =
                "Hi " + profile.getFullName() + ",<br><br>"
                        + "This is a friendly reminder to add your incomes and expenses for today in your Money Manager account.<br><br>"
                        + "<a href=\"" + frontendUrl + "\" "
                        + "style=\"display:inline-block;"
                        + "padding:10px 20px;"
                        + "background-color:#000;"
                        + "color:#fff;"
                        + "text-decoration:none;"
                        + "border-radius:5px;"
                        + "font-weight:bold;\">"
                        + "Go to Money Manager</a>"
                        + "<br><br>Best Regards,<br>"
                        + "Money Manager Team";

        emailService.sendMail(
                profile.getEmail(),
                "Daily Reminder: Add your income and expenses",
                body
        );
    }

    log.info("Job completed: sendDailyIncomeExpenseReminder()");
}


        @Scheduled(cron = "0 0 23 * * *",zone = "IST")
//    @Scheduled(cron = "0 * * * * *",zone = "IST") //for testing
    public void sendDailyExpenseSummary(){
        log.info("Job started: sendDailyExpenseSummary()");
       List<ProfileEntity> profiles= profileRepository.findAll();
       for(ProfileEntity profile:profiles){
           List<ExpenseDto> todaysExpenses=expenseService.getExpensesForUserOnDate(profile.getId(), LocalDate.now());
           if(!todaysExpenses.isEmpty()){
               StringBuilder table = new StringBuilder();

               table.append("<table style='border-collapse:collapse;width:100%;'>");

               table.append("<tr style='background-color:#f2f2f2;'>")
                       .append("<th style='border:1px solid #ddd;padding:8px;'>S.No</th>")
                       .append("<th style='border:1px solid #ddd;padding:8px;'>Name</th>")
                       .append("<th style='border:1px solid #ddd;padding:8px;'>Amount</th>")
                       .append("<th style='border:1px solid #ddd;padding:8px;'>Category</th>")
                       .append("<th style='border:1px solid #ddd;padding:8px;'>Date</th>")
                       .append("</tr>");
               int i=1;
               for(ExpenseDto expense:todaysExpenses){
                   table.append("<tr>")
                           .append("<td style='border:1px solid #ddd;padding:8px;'>").append(i++).append("</td>")
                           .append("<td style='border:1px solid #ddd;padding:8px;'>").append(expense.getName()).append("</td>")
                           .append("<td style='border:1px solid #ddd;padding:8px;'>").append(expense.getAmount()).append("</td>")
                           .append("<td style='border:1px solid #ddd;padding:8px;'>")
                           .append(expense.getCategoryName() != null ? expense.getCategoryName() : "N/A")
                           .append("</td>")
                           .append("<td style='border:1px solid #ddd;padding:8px;'>")
                           .append(expense.getDate())
                           .append("</td>")
                           .append("</tr>");

               }
               String body =
                       "Hi " + profile.getFullName() + ",<br><br>"
                               + "Here is the summary of your expenses today:<br><br>"
                               + table
                               + "<br><br>Best Regards,<br>Money Manager Team";

               emailService.sendMail(
                       profile.getEmail(),
                       "Your Daily Expense Summary",
                       body
               );

           }
       }
               log.info("Job Completed: sendDailyExpenseSummary()");

    }
}
