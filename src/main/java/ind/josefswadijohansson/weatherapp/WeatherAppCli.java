package ind.josefswadijohansson.weatherapp;

import java.util.Scanner;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * @author josefswadijohansson
 */

public class WeatherAppCli {
    public static void main(String[] args) {
        try(Scanner scanner = new Scanner(System.in)) {

            Dotenv dotenv = Dotenv.load();

            System.out.println(""); // To space out the question from the top
            System.out.print("What city do you want the weather forecast for? ");
    
            String cityName = scanner.nextLine().trim();
    
            String apiKey = dotenv.get("WEATHER_APIKEY");
    
            Coordinates cityCoordinates = WeatherService.getCoordinatesFromCityName(cityName, apiKey);
    
            System.out.println("\n============ Current Weather ===========");
            WeatherService.printCurrentWeather(cityName, apiKey);
            System.out.println("============ Weather Forecast ===========");
            WeatherService.printWeatherForecast(cityCoordinates);
            System.out.println("=========================================");
    
        } catch (Exception error) {
            System.out.println("Error: " + error.getMessage());
        }
    }
}
