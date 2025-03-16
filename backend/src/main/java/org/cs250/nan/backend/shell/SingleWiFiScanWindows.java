package org.cs250.nan.backend.shell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class SingleWiFiScanWindows {
    //first method: accepts a string parameter identifying which WiFi interface to use for the scan; must be typed exactly as it appears to the OS,
    // likely need another class or method to retrieve that string (can be done easily)
    //the output from the included method and its overloaded counterpart provide the string argument for the ParseWiFiDataWindows class and its methods
    public static String scan(String interfaceName) {
        //this will return a long string with all teh WiFi data in it. This return has a hardcoded processbuilder to be run if a string parameter is
        // provided, if not this method is overloaded and the other parameter-less version will run
        return runScan(new ProcessBuilder("powershell.exe", "-Command", "netsh wlan show networks interface=\\\"" + interfaceName + "\\\" mode=Bssid | " + "Select-String 'SSID|Signal|Band|Encryption|Authentication|Radio type|Connected stations|Channel utilization'"));
    }

    // Overloaded method without interface name; runs if not parameter is provided, defaults to use the standard onboard WiFi interface that is currently
    // operational and in use by the OS
    public static String scan() {
        return runScan(new ProcessBuilder("powershell.exe", "-Command", "netsh wlan show networks mode=Bssid | " + "Select-String 'SSID|Signal|Band|Encryption|Authentication|Radio type|Connected stations|Channel utilization'"));
    }

    //take the processbuilder and executes it, returning the long string of all the WiFi data collected
    public static String runScan(ProcessBuilder processBuilder) {
        StringBuilder output = new StringBuilder(); //this will receive each line returned by the OS to the standard output (CLI)

        try {
            Process process = processBuilder.start(); //execute the shell command

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream())); //initial capture of data returned by the OS due
            // to the processbuilder command
            String line; //initialize String variable to hold each output line

            while ((line = reader.readLine()) != null) {//while there are still lines to be read from the shell
                output.append(line).append("\n"); // Append each line to the output/stringbuilder object
            }
            if (output.toString().isEmpty()) {
                System.out.println("No information returned from the Wi-Fi interface device; ensure Wi-Fi is enabled.");
                return null;
            }
            return output.toString(); //convert the stringbuilder object to a String

        } catch (IOException e) { //simple exception handling
            e.printStackTrace(); // Print error details (you can log it instead)
            return "IOException";
        }
    }
    //main function for testing purposes
    public static void main(String[] args) {
        System.out.println(scan());
//        System.out.println(scan());
    }

}