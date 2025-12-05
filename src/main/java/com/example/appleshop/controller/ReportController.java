package com.example.appleshop.controller;

import com.example.appleshop.service.ReportService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/report")
public class ReportController {

    @Autowired
    ReportService reportService;

    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    // ✔ Tổng doanh thu
    @GetMapping("/total")
    public ResponseEntity<?> totalRevenue() {
        return ResponseEntity.ok(reportService.totalRevenue());
    }

    // ✔ Doanh thu theo sản phẩm
    @GetMapping("/products")
    public ResponseEntity<?> productRevenue() {
        return ResponseEntity.ok(reportService.productRevenue());
    }

    // ✔ Doanh thu theo ngày
    @GetMapping("/by-date")
    public ResponseEntity<?> revenueByDate(
            @RequestParam String start,
            @RequestParam String end
    ) {
        LocalDate s = LocalDate.parse(start);
        LocalDate e = LocalDate.parse(end);

        return ResponseEntity.ok(reportService.revenueByDate(s, e));
    }



    // ✔ Excel tổng doanh thu theo sản phẩm
    @GetMapping("/excel")
    public void exportExcel(HttpServletResponse response) throws IOException {
        List<Map<String, Object>> data = reportService.productRevenue();
        exportToExcel(response, data, "revenue.xlsx");
    }


    // ⭐ NEW — Xuất Excel thống kê theo ngày
    @GetMapping("/excel-by-date")
    public void exportExcelByDate(
            HttpServletResponse response,
            @RequestParam String start,
            @RequestParam String end
    ) throws IOException {

        LocalDate s = LocalDate.parse(start);
        LocalDate e = LocalDate.parse(end);

        List<Map<String, Object>> data = reportService.revenueByDate(s, e);

        exportToExcel(response, data, "revenue_by_date.xlsx");
    }


    // ---------------------------
    // Hàm chung xuất Excel
    // ---------------------------
    private void exportToExcel(HttpServletResponse response,
                               List<Map<String, Object>> data,
                               String fileName) throws IOException {

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Revenue");

        int rowIndex = 0;

        Row header = sheet.createRow(rowIndex++);
        header.createCell(0).setCellValue("Sản phẩm");
        header.createCell(1).setCellValue("Số lượng");
        header.createCell(2).setCellValue("Doanh thu");

        for (Map<String, Object> row : data) {
            Row r = sheet.createRow(rowIndex++);
            r.createCell(0).setCellValue(row.get("name").toString());
            r.createCell(1).setCellValue(Integer.parseInt(row.get("quantity").toString()));
            r.createCell(2).setCellValue(Double.parseDouble(row.get("revenue").toString()));
        }

        workbook.write(response.getOutputStream());
        workbook.close();
    }
}
