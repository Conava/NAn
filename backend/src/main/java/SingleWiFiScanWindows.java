import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class SingleWiFiScanWindows {

    public static String scan() {
        StringBuilder output = new StringBuilder();

        try {
            ProcessBuilder processBuilder = new ProcessBuilder("netsh", "wlan", "show", "networks", "mode=bssid");
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n"); // Append each line to the output
            }

            return output.toString();

        } catch (IOException e) {
            e.printStackTrace(); // Print error details (you can log it instead)
            return "IOException";
        }
    }
}