package chain_example;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Pattern;

/**
 * Fetch geolocation data from IP address.
 *
 * <p>Validates an IPv4 address, fetches its geolocation data
 * from the ipapi API, and prints the latitude and longitude coordinates
 * to the console.
 *
 * @author Erica Sadun
 * @see <a href="https://ipapi.co/">ipapi.co</a>
 */
public class GeolocationFetcher {
  
  /**
   * Fetches geolocation of an IP address. Main entry point.
   *
   * @param args Command-line arguments. Expects one string-based IPv4 address.
   */
  public static void main(String[] args) {

    // Check for argument
    if (args.length != 1) {
      System.err.println("Usage: call with a valid <ipv4_address>");
      return;
    }

    // Test for IPv4 structure/conformance
    String ipAddress = args[0];
    if (!isValidIPAddress(ipAddress)) {
      System.err.println("Invalid IPv4 address format.");
      return;
    }

    // Fetch Geolocation information
    try {
      JSONObject geolocationData = fetchGeolocationData(ipAddress);
      System.out.print(geolocationData.getDouble("latitude") + " " + 
                       geolocationData.getDouble("longitude") + "\n");
    } catch (IOException e) {
      System.err.println("Error fetching geolocation data: " +
                         e.getMessage());
    }
  }
  
  /**
   * Returns Boolean indicating whether this string is a likely IP address.
   *
   * @apiNote A rough approximation. Not meant for production use.
   *
   * @param ipAddress The string-based IP address to validate
   * @return A Boolean. True if the IP address is likely valid, false otherwise
   */
  private static boolean isValidIPAddress(String ipAddress) {
    // Regular expression to match IPv4 address format
    String ipRegex = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
    return Pattern.matches(ipRegex, ipAddress);
  }
  
  /**
   * Return JSONObject geolocation data for an IP address string
   *
   * @param ipAddress A string-based IP address
   * @return A JSONObject containing Geolocation data for the IP address
   * @throws IOException If an error occurs while fetching geolocation
   */
  private static JSONObject fetchGeolocationData(String ipAddress) throws IOException {
    // See: https://ipapi.co/
    String geolocationEndpointString = "https://ipapi.co/" + ipAddress + "/json/";
    return fetchDataFromURLString(geolocationEndpointString);
  }
  
  /**
   * Fetches data from a URL and returns it as a JSONObject.
   *
   * @param urlString The URL to fetch data from.
   * @return The fetched data as a JSONObject.
   * @throws IOException If an error occurs while fetching data.
   */
  private static JSONObject fetchDataFromURLString(String urlString) throws IOException {
    try {
      URI uri = new URI(urlString);
      HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
      connection.setRequestMethod("GET");
  
      if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
        throw new IOException("HTTP connection error. Code: " + connection.getResponseCode());
      }
  
      BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      StringBuilder response = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) {
        response.append(line);
      }
      reader.close();
  
      return new JSONObject(response.toString());
    } catch (IOException | URISyntaxException e) {
      throw new IOException("Error fetching data from URL: " + e.getMessage());
    }
  }
}
