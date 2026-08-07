package com.healthcheck.service;

import com.healthcheck.model.Alert;
import com.healthcheck.model.Report;
import com.healthcheck.model.Server;
import com.healthcheck.repository.AlertRepository;
import com.healthcheck.repository.ReportRepository;
import com.healthcheck.repository.ServerRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

    private final ReportRepository reportRepository;
    private final ServerRepository serverRepository;
    private final AlertRepository alertRepository;
    private final AuditService auditService;

    private static final String REPORT_DIR = "/home/wafa/healthcheck-backend/reports/";
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm");

    // Rapport quotidien — tous les jours à 7h00
    @Scheduled(cron = "0 0 7 * * *")
    public void generateDailyReport() {
        generateReport("DAILY");
    }

    // Rapport hebdomadaire — chaque lundi à 7h30
    @Scheduled(cron = "0 30 7 * * MON")
    public void generateWeeklyReport() {
        generateReport("WEEKLY");
    }

    // Rapport mensuel — 1er de chaque mois à 8h00
    @Scheduled(cron = "0 0 8 1 * *")
    public void generateMonthlyReport() {
        generateReport("MONTHLY");
    }

    public Report generateReport(String type) {
        Report report = new Report();
        report.setType(type);
        report.setStatus("GENERATED");

        try {
            Files.createDirectories(Paths.get(REPORT_DIR));

            List<Server> servers = serverRepository.findAll();
            List<Alert> alerts = alertRepository.findAll();

            long alertsNew = alerts.stream().filter(a -> "NEW".equals(a.getStatus())).count();
            long alertsAck = alerts.stream().filter(a -> "ACKNOWLEDGED".equals(a.getStatus())).count();
            long alertsRes = alerts.stream().filter(a -> "RESOLVED".equals(a.getStatus())).count();

            report.setTotalServers(servers.size());
            report.setTotalAlerts(alerts.size());
            report.setAlertsNew((int) alertsNew);
            report.setAlertsAcknowledged((int) alertsAck);
            report.setAlertsResolved((int) alertsRes);

            String filename = "report_" + type + "_" + LocalDateTime.now().format(FMT) + ".pdf";
            report.setFilename(filename);

            generatePdf(REPORT_DIR + filename, type, servers, alerts);

            auditService.log("system", "GENERATE_REPORT", "reports", type + " report generated: " + filename, "system");
            log.info("Report generated: {}", filename);

        } catch (Exception e) {
            log.error("Report generation failed: {}", e.getMessage());
            report.setStatus("FAILED");
        }

        return reportRepository.save(report);
    }

    private void generatePdf(String path, String type, List<Server> servers, List<Alert> alerts) throws Exception {
        Document doc = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(doc, new FileOutputStream(path));
        doc.open();

        // Titre
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, new BaseColor(0, 82, 136));
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
        Font cellFont = new Font(Font.FontFamily.HELVETICA, 9);

        Paragraph title = new Paragraph("HPE Health Check - Rapport " + type, titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        doc.add(title);

        Paragraph date = new Paragraph("Généré le : " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                new Font(Font.FontFamily.HELVETICA, 10));
        date.setAlignment(Element.ALIGN_CENTER);
        doc.add(date);
        doc.add(Chunk.NEWLINE);

        // Résumé
        Font sectionFont = new Font(Font.FontFamily.HELVETICA, 13, Font.BOLD);
        doc.add(new Paragraph("Résumé", sectionFont));
        doc.add(Chunk.NEWLINE);

        PdfPTable summary = new PdfPTable(4);
        summary.setWidthPercentage(60);
        addSummaryCell(summary, "Total Serveurs", String.valueOf(servers.size()), new BaseColor(0, 82, 136));
        addSummaryCell(summary, "Total Alertes", String.valueOf(alerts.size()), new BaseColor(200, 100, 0));
        addSummaryCell(summary, "Alertes Nouvelles", String.valueOf(alerts.stream().filter(a -> "NEW".equals(a.getStatus())).count()), new BaseColor(180, 0, 0));
        addSummaryCell(summary, "Alertes Résolues", String.valueOf(alerts.stream().filter(a -> "RESOLVED".equals(a.getStatus())).count()), new BaseColor(0, 150, 0));
        doc.add(summary);
        doc.add(Chunk.NEWLINE);

        // Tableau serveurs
        doc.add(new Paragraph("Inventaire des Serveurs", sectionFont));
        doc.add(Chunk.NEWLINE);

        PdfPTable srvTable = new PdfPTable(6);
        srvTable.setWidthPercentage(100);
        srvTable.setWidths(new float[]{2f, 2f, 2f, 3f, 2f, 1.5f});

        String[] srvHeaders = {"Nom", "IP", "iLO", "Modèle", "Localisation", "Statut"};
        for (String h : srvHeaders) {
            PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
            cell.setBackgroundColor(new BaseColor(0, 82, 136));
            cell.setPadding(5);
            srvTable.addCell(cell);
        }

        for (Server s : servers) {
            srvTable.addCell(new Phrase(s.getName(), cellFont));
            srvTable.addCell(new Phrase(s.getIpAddress(), cellFont));
            srvTable.addCell(new Phrase(s.getIloIp() != null ? s.getIloIp() : "-", cellFont));
            srvTable.addCell(new Phrase(s.getModel() != null ? s.getModel() : "-", cellFont));
            srvTable.addCell(new Phrase(s.getLocation() != null ? s.getLocation() : "-", cellFont));

            PdfPCell statusCell = new PdfPCell(new Phrase(s.getStatus(), cellFont));
            statusCell.setBackgroundColor("UP".equals(s.getStatus()) ? new BaseColor(0, 200, 0) :
                    "DOWN".equals(s.getStatus()) ? new BaseColor(200, 0, 0) : new BaseColor(255, 165, 0));
            srvTable.addCell(statusCell);
        }
        doc.add(srvTable);
        doc.add(Chunk.NEWLINE);

        // Tableau alertes
        doc.add(new Paragraph("Historique des Alertes", sectionFont));
        doc.add(Chunk.NEWLINE);

        PdfPTable alertTable = new PdfPTable(5);
        alertTable.setWidthPercentage(100);
        alertTable.setWidths(new float[]{2f, 2f, 1.5f, 1.5f, 3f});

        String[] alertHeaders = {"Serveur", "Alerte", "Sévérité", "Statut", "Description"};
        for (String h : alertHeaders) {
            PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
            cell.setBackgroundColor(new BaseColor(0, 82, 136));
            cell.setPadding(5);
            alertTable.addCell(cell);
        }

        for (Alert a : alerts) {
            alertTable.addCell(new Phrase(a.getServerName(), cellFont));
            alertTable.addCell(new Phrase(a.getAlertName(), cellFont));
            alertTable.addCell(new Phrase(a.getSeverity(), cellFont));

            PdfPCell statusCell = new PdfPCell(new Phrase(a.getStatus(), cellFont));
            statusCell.setBackgroundColor("NEW".equals(a.getStatus()) ? new BaseColor(200, 0, 0) :
                    "ACKNOWLEDGED".equals(a.getStatus()) ? new BaseColor(255, 165, 0) : new BaseColor(0, 200, 0));
            alertTable.addCell(statusCell);
            alertTable.addCell(new Phrase(a.getDescription() != null ? a.getDescription() : "-", cellFont));
        }
        doc.add(alertTable);
        doc.close();
    }

    private void addSummaryCell(PdfPTable table, String label, String value, BaseColor color) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(color);
        cell.setPadding(8);
        Paragraph p = new Paragraph(label + "\n" + value,
                new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, BaseColor.WHITE));
        p.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(p);
        table.addCell(cell);
    }

    public List<Report> getAll() {
        return reportRepository.findAllByOrderByGeneratedAtDesc();
    }

    public List<Report> getByType(String type) {
        return reportRepository.findByTypeOrderByGeneratedAtDesc(type);
    }

    public String getReportPath(Long id) {
        Report r = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found"));
        return REPORT_DIR + r.getFilename();
    }
}
