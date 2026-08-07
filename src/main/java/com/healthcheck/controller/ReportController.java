package com.healthcheck.controller;

import com.healthcheck.model.Report;
import com.healthcheck.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReportController {

    private final ReportService reportService;

    @GetMapping
    public List<Report> getAll() { return reportService.getAll(); }

    @GetMapping("/type/{type}")
    public List<Report> getByType(@PathVariable String type) {
        return reportService.getByType(type);
    }

    @PostMapping("/generate/{type}")
    public ResponseEntity<Report> generate(@PathVariable String type) {
        String t = type.toUpperCase();
        if (!t.equals("DAILY") && !t.equals("WEEKLY") && !t.equals("MONTHLY"))
            return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(reportService.generateReport(t));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<FileSystemResource> download(@PathVariable Long id) {
        String path = reportService.getReportPath(id);
        File file = new File(path);
        if (!file.exists()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName())
                .contentType(MediaType.APPLICATION_PDF)
                .body(new FileSystemResource(file));
    }
}
