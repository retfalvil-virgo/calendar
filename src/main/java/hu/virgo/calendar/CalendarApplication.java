package hu.virgo.calendar;

import hu.virgo.calendar.domain.model.CalendarProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(scanBasePackages = "hu.virgo.calendar")
@EnableConfigurationProperties(CalendarProperties.class)
public class CalendarApplication {

    public static void main(String[] args) {
        SpringApplication.run(CalendarApplication.class, args);
    }

}
