package chain_example;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * This class retrieves weather data from the National Weather Service API.
 *
 * @author Erica Sadun
 * @version 1.0
 * @see <a href="https://www.weather.gov/documentation/services-web-api">NationalWeatherService API documentation</a>
 */
public class WeatherFetcher {
  
  /**
   * The main entry point for the WeatherFetcher application.
   *
   * <p>This method fetches weather conditions based on longitude and latitude coordinates provided
   * as command-line arguments.
   *
   * @param args Command-line arguments. Expects two arguments: longitude and latitude.
   * @throws IOException If an error occurs while fetching weather data.
   * @see <a href="https://docs.geotools.org/latest/userguide/library/referencing/order.html">Axis Order</a> from the OSGeo project.
   */
  public static void main(String[] args) {
    if (args.length != 2) {
      System.err.println("Usage: java chain_example.WeatherFetcher " +
                         "<longitude> <latitude>");
      return;
    }

    String longitude = args[0];
    String latitude = args[1];

    // Request weather information from the National Weather Service
    try {
      // First, extract a URL with the local forecast information
      JSONObject weatherData = fetchWeatherData(longitude, latitude);
      JSONObject weatherProperties = weatherData.getJSONObject("properties");
      String forecastURLString = weatherProperties.getString("forecast");
  
      // Then break down the forecast JSON to retrievea detailed forecast
      JSONObject forecastData = fetchDataFromURLString(forecastURLString);
      JSONObject forecastProperties = forecastData.getJSONObject("properties");
      JSONArray forecastPeriods = forecastProperties.getJSONArray("periods");
  
      // Ensure there's at least one location to select
      if (forecastPeriods.length() > 0) {
        JSONObject firstForecastProperties = forecastPeriods.getJSONObject(0);
        String forecastString = firstForecastProperties.getString("detailedForecast");
        System.out.println(forecastString);
      } else {
        throw new RuntimeException("Missing forecast information.");
      }
    } catch (IOException e) {
      System.err.println("Error fetching weather data: " + e.getMessage());
    }
  }
  
  /**
   * Fetch weather data from the National Weather Service API.
   *
   * @param longitude Longitude of the location.
   * @param latitude  Latitude of the location.
   * @return Weather data as a JSONObject.
   * @throws IOException If an error occurs while fetching weather data.
   * @see <a href="https://www.weather.gov/documentation/services-web-api">NationalWeatherService API documentation</a>
   * @apiNote Although EPSG:4326 specifies that latitude comes first,
   * many computer systems and software still use longitude and then latitude ordering like
   * this sample code. PostGIS and WFS 1.0 use long/lat. WFS 1.3 and GeoTools use lat/long.
   * Some tooling allows you to swap the expected order with overrides.
   * @see <a href="https://docs.geotools.org/latest/userguide/library/referencing/order.html">Axis Order</a> from the OSGeo project.
   */
  private static JSONObject fetchWeatherData(String longitude, String latitude) throws IOException {
    String weatherEndpoint = "https://api.weather.gov/points/" + longitude + "," + latitude;
    return fetchDataFromURLString(weatherEndpoint);
  }

  /**
   * Fetch data from a URL string, returning a JSON object
   *
   * @param urlString The URL to fetch data from.
   * @return The fetched data as a JSONObject.
   * @throws IOException If an error occurs while fetching data.
   */
  private static JSONObject fetchDataFromURLString(String urlString) throws IOException {
    try {
      URI uri = new URI(urlString);
      HttpClient client = HttpClient.newHttpClient();
      HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
  
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
  
      if (response.statusCode() != 200) {
        throw new IOException("HTTP connection error. Code: " + response.statusCode());
      }
      return new JSONObject(response.body());
  
    } catch (URISyntaxException | InterruptedException e) {
      throw new IOException("Error fetching data from URL: " + e.getMessage());
    }
  }
}
