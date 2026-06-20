package com.nhom1;
import javax.swing.*;
import javax.swing.table.TableModel;
import java.io.File;
import java.io.FileOutputStream;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.awt.Desktop;

public class ExcelExporter {
    public static void exportToExcel(JTable table, File file, String sheetName) {
        // Đảm bảo file có đuôi .xlsx
        if (!file.getName().toLowerCase().endsWith(".xlsx")) {
            file = new File(file.getAbsolutePath() + ".xlsx");
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(sheetName);
            TableModel model = table.getModel();

            // Style cho Header (In đậm, nền xám nhạt)
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            
            // Format cột tiền tệ (nếu cần, ở đây để mặc định text cho đơn giản)
            
            // 1. Tạo Header
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < model.getColumnCount(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(model.getColumnName(i));
                cell.setCellStyle(headerStyle);
            }

            // 2. Ghi Dữ liệu
            for (int i = 0; i < model.getRowCount(); i++) {
                Row row = sheet.createRow(i + 1);
                for (int j = 0; j < model.getColumnCount(); j++) {
                    Object val = model.getValueAt(i, j);
                    if (val != null) {
                        row.createCell(j).setCellValue(val.toString());
                    }
                }
            }
            
            // 3. Auto size cột cho đẹp
            for(int i=0; i<model.getColumnCount(); i++) {
                sheet.autoSizeColumn(i);
            }

            // 4. Lưu file
            try (FileOutputStream out = new FileOutputStream(file)) {
                workbook.write(out);
            }
            JOptionPane.showMessageDialog(null, "Xuất file Excel thành công!\nĐường dẫn: " + file.getAbsolutePath());
            
            // Tùy chọn: Mở file ngay sau khi xuất
            try {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(file);
                }
            } catch (Exception ex) { 
                // Không mở được thì thôi, không báo lỗi
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Lỗi xuất file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}