package ch.bfh.eglim8.spring.hello;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@ConfigurationProperties(prefix = "times-of-day")
@Validated
public record TimesOfDay(@Min(0) @Max(24) int morning,
                         @Min(0) @Max(24) int afternoon,
                         @Min(0) @Max(24) int evening) {
}