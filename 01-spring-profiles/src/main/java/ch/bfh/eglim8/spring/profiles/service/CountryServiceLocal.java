package ch.bfh.eglim8.spring.profiles.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * Local CountrySerivce implementation
 */
@Component
@Profile("local")
public class CountryServiceLocal implements CountryService {

    /*
     * (non-Javadoc)
     *
     * @see ds.service.CountryService#getCountryName(java.lang.String)
     */
    @Override
    public String getCountryName(String countryCode) {
        Locale locale = new Locale("", countryCode);
        return locale.getDisplayCountry();
    }

}
