package org.cs250.nan.backend.scanner;

import com.fazecast.jSerialComm.SerialPort;
import org.cs250.nan.backend.parser.LinuxGPSDataParser;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class LinuxGPSScanner implements GPSScanner {
    private final LinuxGPSDataParser parser = new LinuxGPSDataParser();

    @Override
    public JSONObject scan() throws IOException {
        // 1) find a USB/ACM port
        SerialPort[] ports = SerialPort.getCommPorts();
        Optional<SerialPort> maybe = Arrays.stream(ports)
                .filter(p -> p.getSystemPortName().matches("ttyUSB\\d+|ttyACM\\d+"))
                .findFirst();

        SerialPort port = maybe
                .orElseThrow(() -> new IOException("No GPS serial port found"));

        port.setBaudRate(4800);
        port.setNumDataBits(8);
        port.setNumStopBits(SerialPort.ONE_STOP_BIT);
        port.setParity(SerialPort.NO_PARITY);

        if (!port.openPort()) {
            throw new IOException("Failed to open GPS port " + port.getSystemPortName());
        }

        StringBuilder buf = new StringBuilder();
        Map<String,String> sentences = new HashMap<>();
        boolean firstDiscard = false;

        // collect one each of GPGGA, GPGSA, GPRMC
        while (sentences.size() < 3) {
            if (port.bytesAvailable() > 0) {
                byte[] b = new byte[256];
                int n = port.readBytes(b, b.length);
                for (int i = 0; i < n; i++) {
                    char c = (char) b[i];
                    buf.append(c);
                    if (c == '\n') {
                        String line = buf.toString().trim();
                        buf.setLength(0);
                        if (!firstDiscard) {
                            firstDiscard = true;
                        } else if (line.startsWith("$GPGGA")) {
                            sentences.put("GPGGA", line);
                        } else if (line.startsWith("$GPGSA")) {
                            sentences.put("GPGSA", line);
                        } else if (line.startsWith("$GPRMC")) {
                            sentences.put("GPRMC", line);
                        }
                    }
                }
            }
        }

        port.closePort();

        // merge sentences
        StringBuilder all = new StringBuilder();
        sentences.values().forEach(s -> all.append(s).append("\n"));

        // parse and return JSON
        return parser.parseStringToJSON(all.toString());
    }
}
