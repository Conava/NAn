package org.cs250.nan.backend.shell;

import com.fazecast.jSerialComm.SerialPort;

import java.util.HashMap;
import java.util.Map;

public class SingleGPSScanWindows {
    //this class reads the constant stream of data coming in from the GPS device; this data is broken up into "NMEA sentences"
    //the methods collect those sentences into a single string and returns its; the return is to be used as the argument for
    //the ParseGPSData class

    //This method needs the port name the GPS is using. We can get this with another powershell/cmd command
    //The overloaded method, where no parameter is provided, defaults to "COM3", but that might not always be correct
    public String getAllGPSData(String inputPortName) {
        StringBuilder output = new StringBuilder(); //initilaize stringbuilder, each NMEA sentence will be added to this object
        SerialPort serialPort = SerialPort.getCommPort(inputPortName); //allows interactions with the gps device via it's port
        Map<String, String> gpsSentences = new HashMap<String, String>(); //map to hold unique NMEA sentences; when duplicates occur,
        //they are overwritten in the map by adding the sentence via it's key again

        // Set the baud rate and other serial port options, should probably turn this into a configuration file that is
        // read by another class, passing the arguments to this class/method. These parameters are specific to the gps device
        //and were determined by the deices documentation in the recommended settings
        serialPort.setBaudRate(4800);  // GPS typically uses 4800 baud
        serialPort.setNumDataBits(8);  // 8 data bits
        serialPort.setNumStopBits(1);  // 1 stop bit
        //the two settings below are commented out for now, they may be useful in the future but will likely just be removed
//        serialPort.setParity(SerialPort.NO_PARITY); // No parity
//        serialPort.setFlowControl(SerialPort.FLOW_CONTROL_DISABLED); // No flow control

        // Open the port
        if (serialPort.openPort()) {
            System.out.println("Port opened successfully.");
            StringBuilder sentenceBuilder = new StringBuilder(); //another stringbuilder to build each individual sentence,
            // character by character

            // Read data from the serial port
            boolean firstSentenceDiscarded = false; //flag used to discard the first sentence. This is necessary becasue the
            //GPS device is constantly sending it's data and therefore when we start reading data, we may be starting mid
            //NMEA sentence. We will discard that partial sentence once the newline character is encountered and update
            // this bool flag to true
            while (gpsSentences.size() < 4) { //selected "less than 4" so that we run the loop until 4 types of NMEA sentences
                //are captured. It works this way becasue there is no set pattern to which specific types of NMEA sentence
                //will be reported, so we might read 3 GPGGA sentence, 1 GPGSA setenece, a GPGSV, then finally a GPRMC.
                //To get all the info we need, we keep recording sentences, overwriting duplicates until capturing one of each
                if (serialPort.bytesAvailable() > 0) { //if the gps device is producing data
                    byte[] buffer = new byte[1024]; // Buffer to store incoming data since it is continuously produced
                    int numRead = serialPort.readBytes(buffer, buffer.length); //get currently captured characters from buffer

                    if (numRead > 0) {//if we have 1 or more chatacter to add to the NMEA sentence
                        // Append each character of the new data to the sentence builder
                        for (int i = 0; i < numRead; i++) {
                            char c = (char) buffer[i];
                            sentenceBuilder.append(c);
                            // Check if we've reached the end of a complete NMEA sentence (e.g., a newline or checksum)
                            if (c == '\n') {
                                if (firstSentenceDiscarded) {//if we have already discarded the first partial sentence
                                    String completeSentence = sentenceBuilder.toString().trim(); //clear any whitespace
                                    //check to see which type of NMEA sentence we just collected and add to the map with
                                    //a key corresponding to the sentence type; if the key exists, the data is updated
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
                                    sentenceBuilder.setLength(0); // Clear the builder for the next sentence
                                }
                                else { //if the sentence is the first sentence read, it is likely incomplete
                                    firstSentenceDiscarded = true; //update the bool flag variable
                                    sentenceBuilder.setLength(0); //discard the incomplete NMEA sentence
                                }
                            }
                        }
                    } else { // the device has stopped producing data for some reason; problem external to the class
                        System.out.println("No data received.");
                        return "";
                    }
                }
            }
            //add each sentence from the map to the output stringbuilder, merging all into a single string
            for (String sentence : gpsSentences.values()) {
                output.append(sentence).append("\n");
            }
        } else {
            System.out.println("Failed to open port."); //no device is using the specified port
            return "";
        }
        // Close the port
        serialPort.closePort();
        return output.toString();
    }

    //overloaded method; if not port is specifed, default to "COM3", which seems to be consistent for our gps device
    //we can query the OS to find out which port is used and pass that as the argument to the primary method above
    public String getAllGPSData() {
        return getAllGPSData("COM3");
    }

    //modified version of the primary method; this one ignores the GPGSV sentences as they are slow in coming and provide
    //little inforamtion of value. Nearly identical, only modifications are changing the while loop condition from 4 to 3
    //and removing the GPGSV else-if statement
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

    //overloaded function that defaults to "COM3" as the port name for the GPGSV-ignore method
    public String getGPSDataIgnoreGPGSV() {
        return getGPSDataIgnoreGPGSV("COM3");
    }

    //main for testing, used to compare how long it took to get the all 4 NMEA sentences versus 3, excluding GPGSV
    //it takes about 2-3 seconds longer to get all 4 (3-4 seconds) compared to the GPGSV-ignore method (~0.8-1 second)
    public static void main(String[] args) {
        SingleGPSScanWindows gpsCollector = new SingleGPSScanWindows(); //create object
        long startTime = System.currentTimeMillis(); //start timer
        String result = gpsCollector.getAllGPSData(); //get all 4 NMEA sentences
        long endTime = System.currentTimeMillis(); //Stop timer
        System.out.println(result);
        long elapsedTime = endTime - startTime; //get elapsed time
        System.out.println("Time taken for Task 1: " + elapsedTime + " milliseconds");
        System.out.println("\n\n");
        //do the same but for the GPGSV-ignore method
        startTime = System.currentTimeMillis();
        result = gpsCollector.getGPSDataIgnoreGPGSV();
        endTime = System.currentTimeMillis();
        System.out.println(result);
        elapsedTime = endTime - startTime;
        System.out.println("Time taken for Task 2: " + elapsedTime + " milliseconds");
    }
}
