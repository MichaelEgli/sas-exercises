import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class CountryServiceStep2 {

    public static void getCountryStep2(String code) throws IOException, InterruptedException {

        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://translation-service-nameless-haze-4636.fly.dev/country/" + code))
                    .GET()
                    .build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        System.out.println("Country step2 response: " + response.body());

    }
}
