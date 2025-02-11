import com.fazecast.jSerialComm.SerialPort;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.List;

// will add more comments once I get this pushed to GitHub initially

public class singleLogWifiGpsData {

    public static String getAvailWifiInterfaces(Scanner scanner) {

        StringBuilder output = new StringBuilder();

        try {
            ProcessBuilder processBuilder = new ProcessBuilder("netsh", "wlan", "show", "interfaces");
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n"); // Append each line to the output
            }
            reader.close(); // Close the reader

            System.out.println(output.toString()); // Return the full output as a string

            do {
                System.out.print("Enter the name of the interface to be used for the Wifi scan exactly as it appears above:\n\t"); // Prompt the user
                String wifiInterfaceName = scanner.nextLine(); // Read the user input
                if (wifiInterfaceName.equals("QUIT")) {
                    return "Aborted";
                }
//                System.out.println("Interface Name: " + wifiInterfaceName); // Display the input
                try {
                    StringBuilder output2 = new StringBuilder();
                    ProcessBuilder processBuilder2 = new ProcessBuilder("netsh", "interface", "show", "interface", "name=" + wifiInterfaceName);
                    Process process2 = processBuilder2.start();

                    BufferedReader reader2 = new BufferedReader(new InputStreamReader(process2.getInputStream()));
                    String line2;

                    while ((line2 = reader2.readLine()) != null) {
                        output2.append(line2).append("\n"); // Append each line to the output
                    }
                    reader2.close(); // Close the reader

                    System.out.println(output2.toString());
                    if (!output2.toString().trim().equals("An interface with this name is not registered with the router.")) {
                        return wifiInterfaceName;
                    } else {
                        System.out.println("Invalid interface name, try again or type QUIT to abort.\n\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace(); // Print error details (you can log it instead)
                    return "IOException";
                }
            } while (true);
        } catch (IOException e) {
            e.printStackTrace(); // Print error details (you can log it instead)
            return "IOException";
        }
    }

    public static String netshScan(String wifiInterfaceName, Scanner scanner) {
//        String selection;
//
//        System.out.print("(1)  Single scan\n(2) Continuous scan\nType the number (1 or 2) of the desired scan type:\n\t"); // Prompt the user
//        String scanType = scanner.nextLine(); // Read the user input
//        if (scanType.equals("1")) {
//            selection = "SINGLE";
//        } else if (scanType.equals("2")) {
//            selection = "CONTINUOUS";
//        } else {
//            System.out.println("Invalid scanType");
//            return "Invalid";
//        }
//
//        System.out.print("Beginning " + selection + " scan...\n");

        StringBuilder output = new StringBuilder();

        try {
            ProcessBuilder processBuilder = new ProcessBuilder("netsh", "wlan", "show", "networks", "interface=" + wifiInterfaceName, "mode=bssid");
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n"); // Append each line to the output
            }

            System.out.println(output.toString());

            return "Done";

        } catch (IOException e) {
            e.printStackTrace(); // Print error details (you can log it instead)
            return "IOException";
        }
    }

    public static String getGpsDevice(Scanner scanner) {
        String device;
        StringBuilder output = new StringBuilder();
        String[] command = {"powershell", "-Command", "[System.IO.Ports.SerialPort]::GetPortNames()"};
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n"); // Append each line to the output
            }

            device = output.toString().trim();
            if (device.isEmpty()) {
                System.out.println("No GPS device found, continuing without logging GPS coordinates");
            } else {
                System.out.println("GPS device: " + device);
            }
            return device;

        } catch (IOException e) {
            e.printStackTrace(); // Print error details (you can log it instead)
            return "";
        }
    }

    public static void readGPSSentences(String portName) {
        // Open the specified serial port
        SerialPort serialPort = SerialPort.getCommPort(portName);

        // Set the baud rate and other serial port options
        serialPort.setBaudRate(4800);  // GPS typically uses 4800 baud
        serialPort.setNumDataBits(8);  // 8 data bits
        serialPort.setNumStopBits(1);  // 1 stop bit
        serialPort.setParity(SerialPort.NO_PARITY); // No parity
//        serialPort.setFlowControl(SerialPort.FLOW_CONTROL_DISABLED); // No flow control

        long startTime = System.currentTimeMillis(); // Record the start time
        long timeout = 5000; // 5 seconds timeout

        // Try to open the port for up to 5 seconds
        while (System.currentTimeMillis() - startTime < timeout) {
            if (serialPort.openPort()) {
                break; // Exit the loop if the port is successfully opened
            } else {
                System.out.println("Failed to open port, retrying...");
                try {
                    Thread.sleep(500); // Wait for half a second before trying again
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        // Open the port
        if (serialPort.openPort()) {
            System.out.println("GPS device Port: " + portName + " opened successfully. Collecting NMEA data, please wait...");
            StringBuilder sentenceBuilder = new StringBuilder();

            // Create a hashtable to store sentences by type
            Hashtable<String, List<String>> sentences = new Hashtable<>();
            String[] sentenceTypes = {"GPGGA", "GPGSA", "GPRMC", "GPGSV"};
            int sentencesCaptured = 0;

            // Read data from the serial port
            while (sentencesCaptured < sentenceTypes.length) {
                if (serialPort.bytesAvailable() > 0) {
                    byte[] buffer = new byte[1024]; // Buffer to store incoming data
                    int numRead = serialPort.readBytes(buffer, buffer.length);

                    if (numRead > 0) {
                        // Append the new data to the sentence builder
                        for (int i = 0; i < numRead; i++) {
                            char c = (char) buffer[i];
                            sentenceBuilder.append(c);
                            // Check if we've reached the end of a complete NMEA sentence (e.g., a newline or checksum)
                            if (c == '\n') {
                                String completeSentence = sentenceBuilder.toString().trim();
//                                System.out.println(completeSentence);
                                for (String type : sentenceTypes) {
                                    if (completeSentence.startsWith("$" + type)) {
//                                        System.out.println("Captured sentence: " + completeSentence);
                                        // Split the sentence into elements and store in the hashtable
                                        if (!sentences.containsKey(type)) {
                                            List<String> elements = new ArrayList<>();
                                            String[] parts = completeSentence.substring(1).split(","); // Remove '$' and split by commas
                                            for (String part : parts) {
                                                elements.add(part.trim());
                                            }
                                            sentences.put(type, elements);
                                            sentencesCaptured++;
                                        }
                                        // If we've captured all types, break out of the loop
                                        if (sentencesCaptured == sentenceTypes.length) {
                                            break;
                                        }
                                    }
                                }
                                sentenceBuilder.setLength(0); // Clear the builder for the next sentence
                            }
                        }
                    } else {
                        System.out.println("No data received.");
                    }
                }
            }
            // Print the captured sentences
            System.out.println("Captured GPS/NMEA data:");
            for (String type : sentenceTypes) {
                List<String> elements = sentences.get(type);
                if (elements != null) {
                    System.out.println(type + ": " + elements);
                }
            }
        } else {
            System.out.println("Failed to open port.");
        }
        // Close the port
        serialPort.closePort();
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String wifiInterfaceName;
        String netshOutput;
        String gpsDevice;



        wifiInterfaceName = getAvailWifiInterfaces(scanner);
        if (wifiInterfaceName != null && !wifiInterfaceName.equals("IOException") && !wifiInterfaceName.equals("QUIT") && !wifiInterfaceName.equals("Aborted")) {
            System.out.println("Valid Wi-Fi interface name");
        } else {
            System.out.println("Invalid Wi-Fi interface name or IOException - QUITTING...");
            scanner.close();
            return;
        }

        gpsDevice = getGpsDevice(scanner);

        netshOutput = netshScan(wifiInterfaceName, scanner);

        if (!gpsDevice.isEmpty()) { //if a GPS device was detected, call the function to read the GPS data
            readGPSSentences(gpsDevice);
        }

        if (netshOutput.equals("IOException")) {
            System.out.println("netsh failed");
        }
        scanner.close();
    }

}