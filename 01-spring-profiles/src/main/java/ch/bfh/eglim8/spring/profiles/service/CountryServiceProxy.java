package ch.bfh.eglim8.spring.profiles.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Proxy to implement remote call to the country service
 */
@Component
@Profile("remote")
public class CountryServiceProxy implements CountryService {

    private static final Logger LOGGER = Logger.getLogger(CountryServiceProxy.class
            .getName());

    private final String dsServerUrl = "https://translation-service-nameless-haze-4636.fly.dev/country/";

    /*
     * (non-Javadoc)
     *
     * @see ds.service.CountryService#getCountryName(java.lang.String)
     */
    @Override
    public String getCountryName(String countryCode) {
        try {
            URL url = new URL(dsServerUrl + countryCode);
            return this.convertStreamToString((InputStream) url.getContent());
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Problem while communicating with server occured", e);
            return null;
        }
    }

    /**
     * Converts an input stream to a single string
     *
     * @param is stream
     * @return string representation of the input stream
     * @throws IOException  if stream cannot be read
     */
    private String convertStreamToString(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        return sb.toString();
    }

}
