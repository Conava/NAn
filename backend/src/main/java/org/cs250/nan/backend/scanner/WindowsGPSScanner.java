package org.cs250.nan.backend.scanner;

import com.fazecast.jSerialComm.SerialPort;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class WindowsGPSScanner implements GPSScanner {
    @Override
    public JSONObject scan() throws IOException {
        SerialPort port = SerialPort.getCommPorts()[0];
        port.setBaudRate(4800);
        port.openPort();

        var builder = new StringBuilder();
        Map<String, String> sentences = new HashMap<>();
        boolean firstDiscard = false;

        while (sentences.size() < 3) {
            if (port.bytesAvailable() > 0) {
                byte[] buf = new byte[256];
                int n = port.readBytes(buf, buf.length);
                for (int i = 0; i < n; i++) {
                    char c = (char) buf[i];
                    builder.append(c);
                    if (c == '\n') {
                        if (firstDiscard) {
                            String s = builder.toString().trim();
                            if (s.startsWith("$GPGGA")) sentences.put("GPGGA", s);
                            else if (s.startsWith("$GPGSA")) sentences.put("GPGSA", s);
                            else if (s.startsWith("$GPRMC")) sentences.put("GPRMC", s);
                        } else {
                            firstDiscard = true;
                        }
                        builder.setLength(0);
                    }
                }
            }
        }
        port.closePort();
        var out = new StringBuilder();
        sentences.values().forEach(line -> out.append(line).append("\n"));
        return org.cs250.nan.backend.parser.GPSDataParser.parseStringToJSON(out.toString());
    }
}