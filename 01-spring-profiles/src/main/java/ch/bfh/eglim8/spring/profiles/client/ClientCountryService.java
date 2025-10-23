package ch.bfh.eglim8.spring.profiles.client;

import ch.bfh.eglim8.spring.profiles.service.CountryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class ClientCountryService implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientCountryService.class.getName());

    private final CountryService countryService;

     public ClientCountryService(CountryService countryService) {
        this.countryService = countryService;
    }

    @Override
    public void run(ApplicationArguments args) {
        LOGGER.info("Returned country name: {}", countryService.getCountryName("FI"));
    }


}
