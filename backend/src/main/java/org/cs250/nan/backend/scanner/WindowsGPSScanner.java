package org.cs250.nan.backend.scanner;

import com.fazecast.jSerialComm.SerialPort;
import org.cs250.nan.backend.parser.WindowsGPSDataParser;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Windows GPS scanner using original parsing logic.
 */
@Component
public class WindowsGPSScanner implements GPSScanner {
    @Override
    public JSONObject scan() throws IOException {
        SerialPort port = SerialPort.getCommPorts()[0];
        port.setBaudRate(4800);
        port.openPort();

        boolean firstDiscard = false;
        Map<String, String> sentences = new HashMap<>();
        StringBuilder buf = new StringBuilder();
        while (sentences.size() < 3) {
            if (port.bytesAvailable() > 0) {
                byte[] tmp = new byte[256];
                int n = port.readBytes(tmp, tmp.length);
                for (int i = 0; i < n; i++) {
                    char c = (char) tmp[i]; buf.append(c);
                    if (c == '\n') {
                        if (firstDiscard) {
                            String line = buf.toString().trim();
                            if (line.startsWith("$GPGGA")||line.startsWith("$GPGSA")||line.startsWith("$GPRMC")) {
                                sentences.put(line.substring(1,5), line);
                            }
                        } else {
                            firstDiscard = true;
                        }
                        buf.setLength(0);
                    }
                }
            }
        }
        port.closePort();
        StringBuilder all = new StringBuilder();
        sentences.values().forEach(s -> all.append(s).append("\n"));
        return WindowsGPSDataParser.parseGPS(all.toString());
    }
}