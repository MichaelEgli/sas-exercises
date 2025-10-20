import java.util.Locale;

public class CountryServiceStep1 {

    public static void getCountry(String code) {
        System.out.println("Hello Country Service");

        Locale country = new Locale("English", code);
        System.out.println("country: " + country.getDisplayCountry());
    }
}
