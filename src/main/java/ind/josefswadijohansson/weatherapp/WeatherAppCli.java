package ind.josefswadijohansson.weatherapp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import org.json.JSONObject;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * @author josefswadijohansson
 */

public class WeatherAppCli {
    public static void main(String[] args) {
        
        try(Scanner scanner = new Scanner(System.in)){
            Dotenv dotenv = Dotenv.load();

            System.out.println(""); // To space out the question from the top
            System.out.print("What city do you want the weather forecast for? ");
    
            String city = scanner.nextLine().trim();
    
            String apiKey = dotenv.get("WEATHER_APIKEY");
            String urlString = "https://api.openweathermap.org/data/2.5/weather?q=" + city.replace(" ", "%20") + "&appid=" + apiKey + "&units=metric";
    
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))){
                
                StringBuilder response = new StringBuilder();
    
                String line;
    
                while((line = reader.readLine()) != null){
                    response.append(line);
                }
    
                reader.close();
    
                JSONObject jsonResponse = new JSONObject(response.toString());
                String weather = jsonResponse.getJSONArray("weather").getJSONObject(0).getString("description");
                double temperaturInCelcius = jsonResponse.getJSONObject("main").getDouble("temp");
    
                System.out.println("");
                System.out.println(String.format("Weather in %s is : %s °C - %s", city, temperaturInCelcius, weather)) ;
    
            } 
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        } 
    }
}
