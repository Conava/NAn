import com.fazecast.jSerialComm.SerialPort;
import java.util.HashMap;
import java.util.Map;

public class SingleGPSScanWindows {

    public String getAllGPSData(String inputPortName) {
        StringBuilder output = new StringBuilder();
        SerialPort serialPort = SerialPort.getCommPort(inputPortName);
        Map<String, String> gpsSentences = new HashMap<String, String>();

        // Set the baud rate and other serial port options, should probably turn this into a configuration file that is read by another class, passing the arguments to this class/method
        serialPort.setBaudRate(4800);  // GPS typically uses 4800 baud
        serialPort.setNumDataBits(8);  // 8 data bits
        serialPort.setNumStopBits(1);  // 1 stop bit
        serialPort.setParity(SerialPort.NO_PARITY); // No parity
//        serialPort.setFlowControl(SerialPort.FLOW_CONTROL_DISABLED); // No flow control

        // Open the port
        if (serialPort.openPort()) {
            System.out.println("Port opened successfully.");
            StringBuilder sentenceBuilder = new StringBuilder();

            // Read data from the serial port
            boolean firstSentenceDiscarded = false;
            while (gpsSentences.size() < 4) {
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
                                if (firstSentenceDiscarded) {
                                    String completeSentence = sentenceBuilder.toString().trim();
                                    if (completeSentence.startsWith("$GPGGA")) {
                                        gpsSentences.put("GPGGA", completeSentence);
                                    }
                                    else if (completeSentence.startsWith("$GPGSA")) {
                                        gpsSentences.put("GPGSA", completeSentence);
                                    }
                                    else if (completeSentence.startsWith("$GPRMC")) {
                                        gpsSentences.put("GPRMC", completeSentence);
                                    }
                                    else if (completeSentence.startsWith("$GPGSV")) {
                                        gpsSentences.put("GPGSV", completeSentence);
                                    }
//                                    System.out.println(completeSentence);
                                    sentenceBuilder.setLength(0); // Clear the builder for the next sentence
                                }
                                else {
                                    firstSentenceDiscarded = true;
                                    sentenceBuilder.setLength(0);
                                }
                            }
                        }
                    } else {
                        System.out.println("No data received.");
                        return "";
                    }
                }
            }
            for (String sentence : gpsSentences.values()) {
                output.append(sentence).append("\n");
            }
        } else {
            System.out.println("Failed to open port.");
            return "";
        }
        // Close the port
        serialPort.closePort();
        return output.toString();
    }

    public String getAllGPSData() {
        return getAllGPSData("COM3");
    }

    public String getGPSDataIgnoreGPGSV(String inputPortName) {
        StringBuilder output = new StringBuilder();
        SerialPort serialPort = SerialPort.getCommPort(inputPortName);
        Map<String, String> gpsSentences = new HashMap<String, String>();

        // Set the baud rate and other serial port options, should probably turn this into a configuration file that is read by another class, passing the arguments to this class/method
        serialPort.setBaudRate(4800);  // GPS typically uses 4800 baud
        serialPort.setNumDataBits(8);  // 8 data bits
        serialPort.setNumStopBits(1);  // 1 stop bit
        serialPort.setParity(SerialPort.NO_PARITY); // No parity
//        serialPort.setFlowControl(SerialPort.FLOW_CONTROL_DISABLED); // No flow control

        // Open the port
        if (serialPort.openPort()) {
            System.out.println("Port opened successfully.");
            StringBuilder sentenceBuilder = new StringBuilder();

            // Read data from the serial port
            boolean firstSentenceDiscarded = false;
            while (gpsSentences.size() < 3) {
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
                                if (firstSentenceDiscarded) {
                                    String completeSentence = sentenceBuilder.toString().trim();
                                    if (completeSentence.startsWith("$GPGGA")) {
                                        gpsSentences.put("GPGGA", completeSentence);
                                    }
                                    else if (completeSentence.startsWith("$GPGSA")) {
                                        gpsSentences.put("GPGSA", completeSentence);
                                    }
                                    else if (completeSentence.startsWith("$GPRMC")) {
                                        gpsSentences.put("GPRMC", completeSentence);
                                    }
//                                    System.out.println(completeSentence);
                                    sentenceBuilder.setLength(0); // Clear the builder for the next sentence
                                }
                                else {
                                    firstSentenceDiscarded = true;
                                    sentenceBuilder.setLength(0);
                                }
                            }
                        }
                    } else {
                        System.out.println("No data received.");
                        return "";
                    }
                }
            }
            for (String sentence : gpsSentences.values()) {
                output.append(sentence).append("\n");
            }
        } else {
            System.out.println("Failed to open port.");
            return "";
        }
        // Close the port
        serialPort.closePort();
        return output.toString();
    }

    public String getGPSDataIgnoreGPGSV() {
        return getGPSDataIgnoreGPGSV("COM3");
    }

    public static void main(String[] args) {
        SingleGPSScanWindows gpsCollector = new SingleGPSScanWindows();
        long startTime = System.currentTimeMillis();
        String result = gpsCollector.getAllGPSData();
        long endTime = System.currentTimeMillis();
        System.out.println(result);
        long elapsedTime = endTime - startTime;
        System.out.println("Time taken for Task 1: " + elapsedTime + " milliseconds");
        System.out.println("\n\n");
        startTime = System.currentTimeMillis();
        result = gpsCollector.getGPSDataIgnoreGPGSV();
        endTime = System.currentTimeMillis();
        System.out.println(result);
        elapsedTime = endTime - startTime;
        System.out.println("Time taken for Task 2: " + elapsedTime + " milliseconds");
    }
}
