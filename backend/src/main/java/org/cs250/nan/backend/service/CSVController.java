package org.cs250.nan.backend.service;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;

@RestController
@RequestMapping("/api")
public class CSVController {

    @GetMapping("/data")
    public ResponseEntity<Resource> getCsv() throws IOException {
        File file = new File("data/latest.csv"); // adjust this path
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=" + file.getName())
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(resource);
    }
}
