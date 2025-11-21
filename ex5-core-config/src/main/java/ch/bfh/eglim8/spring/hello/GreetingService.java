package ch.bfh.eglim8.spring.hello;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.logging.Logger;

@Service
public class GreetingService {

    private final TimesOfDay timesOfDay;

    public GreetingService(TimesOfDay timesOfDay) {this.timesOfDay = timesOfDay;}

    public String getGreeting() {
        return "Good " + getTimeOfDay();
    }

    public String getTimeOfDay() {
        int hour = LocalTime.now().getHour();
        System.out.println("hour: " + hour);
        if (hour < timesOfDay.morning()) return "night";
        else if (hour < timesOfDay.afternoon()) return "morning";
        else if (hour < timesOfDay.evening()) return "afternoon";
        else return "evening";
    }
}
