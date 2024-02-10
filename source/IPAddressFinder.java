package chain_example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.file.Paths;

/**
 * Retrieve the caller IP address via the ipify.org service.
 *
 * @author Erica Sadun
 * @see <a href="https://www.ipify.org">ipify.org</a>
 */
public class IPAddressFinder {
  
  /**
   * Retrieve the user's IP address and print it.
   *
   * @param args Command line arguments are ignored
   */
  public static void main(String[] args) {
    try {
      String ipAddress = getPublicIPAddress();
      System.out.println(ipAddress);
    } catch (IOException exception) {
      System.err.println("Error retrieving IP address: " + exception.getMessage());
    }
  }
  
  /**
   * Retrieve the active IP address with ipify.org.
   *
   * @return A String with the caller's public IP address
   * @throws IOException If an error occurs while retrieving the IP address
   */
  public static String getPublicIPAddress() throws IOException {

    // Open a connection to api.ipify.org, an IP-reporting microservice
    // See https://www.ipify.org
    String ipAddressEndpointString = "https://api.ipify.org";
    URI uri = URI.create(ipAddressEndpointString);
    HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
    connection.setRequestMethod("GET");

    if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
      throw new IOException("HTTP connection error. Code: " + connection.getResponseCode());
    }

    // Read stream from connection and return the data as a string
    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
    StringBuilder response = new StringBuilder();

    String line;
    while ((line = reader.readLine()) != null) {
      response.append(line);
    }
    reader.close();

    return response.toString();
  }
}
