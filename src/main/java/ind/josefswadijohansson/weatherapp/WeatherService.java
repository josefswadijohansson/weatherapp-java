/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package ind.josefswadijohansson.weatherapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Josef
 */
public class WeatherService {

    public static void printCurrentWeather(String cityName, String apiKey){
        try {
            String currentWeatherURLString = "https://api.openweathermap.org/data/2.5/weather?q=" + cityName.replace(" ", "%20") + "&appid=" + apiKey + "&units=metric";

            JSONObject jsonResponse = fetchJsonFromUrl(currentWeatherURLString);

            if (jsonResponse == null) return;   //Check that the response isn't empty

            String weather = jsonResponse.getJSONArray("weather").getJSONObject(0).getString("description");
            double temperaturInCelcius = jsonResponse.getJSONObject("main").getDouble("temp");

            System.out.println(String.format("Current weather in %s, is right now : %s °C | %s", cityName, temperaturInCelcius, weather)) ;

        } catch (JSONException error) {
            System.out.println("Error: " + error.getMessage());
        }
    }

    public static Coordinates getCoordinatesFromCityName(String cityName, String apiKey){
        try {
            String geocodingURLString = "https://api.openweathermap.org/geo/1.0/direct?q=" + cityName.replace(" ", "%20") + "&limit=1&appid=" + apiKey;

            JSONArray jsonArray = fetchJsonArrayFromUrl(geocodingURLString);
            JSONObject jsonResponse = jsonArray.getJSONObject(0);

            double latitude = jsonResponse.getDouble("lat");
            double longitude = jsonResponse.getDouble("lon");

            return new Coordinates(String.valueOf(latitude), String.valueOf(longitude));
        } catch (JSONException error) {
            System.out.println("Error: " + error.getMessage());
        }

        return new Coordinates("", "");
    }

    public static void printWeatherForecast(Coordinates coordinates){
        try{
            String weatherForecastURLString = "https://api.open-meteo.com/v1/forecast?latitude=" + coordinates.latitude + 
                                                                        "&longitude=" + coordinates.longtitude + "&daily=weather_code,temperature_2m_max,temperature_2m_min&timezone=auto";
            
            JSONObject jsonResponse = fetchJsonFromUrl(weatherForecastURLString);

            JSONArray forecastDates = jsonResponse.getJSONObject("daily").getJSONArray("time");
            JSONArray forecastMaxTemperatures = jsonResponse.getJSONObject("daily").getJSONArray("temperature_2m_max");
            JSONArray forecastMinTemperatures = jsonResponse.getJSONObject("daily").getJSONArray("temperature_2m_min");
            JSONArray forecastWeatherCode = jsonResponse.getJSONObject("daily").getJSONArray("weather_code");

            for(int i = 0; i < forecastDates.length(); i++){

                LocalDate specificDate = LocalDate.parse(forecastDates.getString(i));
                DayOfWeek dayOfWeek = specificDate.getDayOfWeek();
                
                String dayResult = String.format("%s : %.2f °C | %.2f °C | %s", dayOfWeek.toString(), forecastMinTemperatures.getDouble(i), forecastMaxTemperatures.getDouble(i), getWeatherFromWMOCode(forecastWeatherCode.getInt(i)));
                
                System.out.println(dayResult);
            }
        } catch (JSONException error) {
            System.out.println("Error: " + error.getMessage());
        }
    }

    public static String getWeatherFromWMOCode(int wmoCode){
        return switch (wmoCode) {
            case 0 -> "Clear sky";
            case 1 -> "Mainly clear";
            case 2 -> "Partly cloudy";
            case 3 -> "Overcast";
            case 45 -> "Fog";
            case 48 -> "Depositing rime fog";
            case 51 -> "Drizzle: Light intensity";
            case 53 -> "Drizzle: Moderate intensity";
            case 55 -> "Drizzle: Dense intensity";
            case 56 -> "Freezing Drizzle: Light intensity";
            case 57 -> "Freezing Drizzle: Dense intensity";
            case 61 -> "Rain: Slight intensity";
            case 63 -> "Rain: Moderate intensity";
            case 65 -> "Rain: Heavy intensity";
            case 66 -> "Freezing Rain: Light intensity";
            case 67 -> "Freezing Rain: Heavy intensity";
            case 71 -> "Snow fall: Slight intensity";
            case 73 -> "Snow fall: Moderate intensity";
            case 75 -> "Snow fall: Heavy intensity";
            case 77 -> "Snow grains";
            case 80 -> "Rain showers: Slight";
            case 81 -> "Rain showers: Moderate";
            case 82 -> "Rain showers: Violent";
            case 85 -> "Snow showers: Slight";
            case 86 -> "Snow showers: Heavy";
            case 95 -> "Thunderstorm: Slight or moderate";
            case 96 -> "Thunderstorm with slight hail";
            case 99 -> "Thunderstorm with heavy hail";
            default -> "";
        };
    }

    /**
     * Fetches JSON response from a given URL.
    */
    private static JSONObject fetchJsonFromUrl(String urlString) {
        try {
            String response = getHttpResponse(urlString);
            return response != null ? new JSONObject(response) : null;
        } catch (JSONException e) {
            System.out.println("Invalid JSON response: " + e.getMessage());
            return null;
        }
    }

    /**
     * Fetches JSON Array response from a given URL.
    */
    private static JSONArray fetchJsonArrayFromUrl(String urlString) {
        try {
            String response = getHttpResponse(urlString);
            return response != null ? new JSONArray(response) : null;
        } catch (JSONException e) {
            System.out.println("Invalid JSON response: " + e.getMessage());
            return null;
        }
    }

    /**
     * Makes an HTTP GET request and returns the response as a String.
    */
    private static String getHttpResponse(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                return response.toString();
            }
        } catch (IOException e) {
            System.out.println("Network error: " + e.getMessage());
            return null;
        }
    }
}
