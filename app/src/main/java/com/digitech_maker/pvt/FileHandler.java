package com.digitech_maker.pvt;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FileHandler {
    Context ctx;
    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION = 1;

    private Sheet sheet = null;
    private final String EXCEL_SHEET_NAME = "Sheet1";
    private final String TAG = "ERROR";
    private Cell cell = null;
    private Workbook workbook = new HSSFWorkbook();
    private CellStyle headerCellStyle = null;
    private final OkHttpClient client = new OkHttpClient();

    public FileHandler(Context ctx) {
        this.ctx = ctx;
    }

    public void exportFileKeepData(Object[] hsls) {
        try {
            int writeExternalStoragePermission = ContextCompat.checkSelfPermission(ctx, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (writeExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) ctx, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION);
            }

            DateFormat df = new SimpleDateFormat("_dd_MM_yyyy_h_m_s");
            File filedir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + File.separator + "pvt");
            File file = new File(filedir, "pvt" + df.format(new Date()) + ".txt");
            boolean res = filedir.mkdirs();
            if (!filedir.exists()) {
                file = new File(ctx.getFilesDir(), "pvt" + df.format(new Date()) + ".txt");
            }
            FileWriter writer = new FileWriter(file, true);
            for (Object obj : hsls) {
                Hasil hsl = (Hasil) obj;
                String content = formatTestData(hsl);
                writer.append(content);
                writer.flush();
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean exportXlsKeepData(Object[] hsls) {
        boolean isWorkbookWrittenIntoStorage = false;
        DateFormat df = new SimpleDateFormat("_dd_MM_yyyy_h_m_s");
        File filedir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + File.separator + "pvt");
        File thefile = new File(filedir, "pvt" + df.format(new Date()) + ".xls");
        boolean res = filedir.mkdirs();
        if (!filedir.exists()) {
            thefile = new File(ctx.getFilesDir(), "pvt" + df.format(new Date()) + ".txt");
        }
        workbook = new HSSFWorkbook();
        this.setHeaderCellStyle();

        for (Object obj : hsls) {
            Hasil hsl = (Hasil) obj;
            sheet = workbook.createSheet(hsl.getNamadata() + "-" + hsl.getNamaObservant().substring(0, 5));
            sheet.setColumnWidth(0, (15 * 400));
            sheet.setColumnWidth(1, (15 * 600));
            this.setHeaderRow(hsl);
            fillDataIntoExcel(hsl);
        }

        isWorkbookWrittenIntoStorage = storeExcelInStorage(this.ctx, thefile);

        return isWorkbookWrittenIntoStorage;
    }

    private void setHeaderCellStyle() {
        Font newFont = workbook.createFont();
        newFont.setBoldweight((short) 3);
        newFont.setColor(HSSFColor.BLACK.index);
        newFont.setFontHeightInPoints((short) 14);

        headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFillForegroundColor(HSSFColor.AQUA.index);
        headerCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        headerCellStyle.setAlignment(CellStyle.ALIGN_CENTER);
        headerCellStyle.setBorderTop((short) 2);
        headerCellStyle.setBorderLeft((short) 2);
        headerCellStyle.setBorderRight((short) 2);
        headerCellStyle.setBorderBottom((short) 1);
        headerCellStyle.setFont(newFont);
    }

    private CellStyle setBoldCellStyle() {
        Font newFont = workbook.createFont();
        newFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        CellStyle cstyle = workbook.createCellStyle();
        cstyle.setFont(newFont);
        return cstyle;
    }

    private CellStyle setBodyCellStyle() {
        Font newFont = workbook.createFont();
        newFont.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
        CellStyle cstyle = workbook.createCellStyle();
        cstyle.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);
        cstyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        cstyle.setAlignment(CellStyle.ALIGN_RIGHT);
        cstyle.setBorderTop((short) 1);
        cstyle.setBorderLeft((short) 1);
        cstyle.setBorderRight((short) 1);
        cstyle.setBorderBottom((short) 1);
        cstyle.setFont(newFont);
        return cstyle;
    }

    private void setHeaderRow(Hasil hsl) {
        String jenistest = hsl.getJenistest() == 1 ? "Visual" : "Audio";
        String hasilteks = getHasilText(hsl.getRataRata());
        CellStyle boldcs = this.setBoldCellStyle();

        Row headerRow = sheet.createRow(0);
        Font newFont = workbook.createFont();
        newFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

        CellStyle cstyle = workbook.createCellStyle();
        cstyle.setFont(newFont);

        cell = headerRow.createCell(0);
        cell.setCellValue("ID");
        cell.setCellStyle(headerCellStyle);

        cell = headerRow.createCell(1);
        cell.setCellValue(hsl.getNamadata());
        cell.setCellStyle(cstyle);

        headerRow = sheet.createRow(1);
        cell = headerRow.createCell(0);
        cell.setCellValue("Nama Observant");
        cell.setCellStyle(headerCellStyle);

        cell = headerRow.createCell(1);
        cell.setCellValue(hsl.getNamaObservant());
        cell.setCellStyle(cstyle);

        headerRow = sheet.createRow(2);
        cell = headerRow.createCell(0);
        cell.setCellValue("Tanggal");
        cell.setCellStyle(headerCellStyle);

        cell = headerRow.createCell(1);
        cell.setCellValue(hsl.getTanggal());
        cell.setCellStyle(cstyle);

        headerRow = sheet.createRow(4);
        cell = headerRow.createCell(0);
        cell.setCellValue("Jenistest");
        cell.setCellStyle(headerCellStyle);

        cell = headerRow.createCell(1);
        cell.setCellValue(jenistest);
        cell.setCellStyle(cstyle);


        headerRow = sheet.createRow(6);
        cell = headerRow.createCell(0);
        cell.setCellValue("Hasil (ms)");
        cell.setCellStyle(headerCellStyle);

        cell = headerRow.createCell(1);
        cell.setCellValue(hsl.getRataRata());
        cell.setCellStyle(cstyle);

        headerRow = sheet.createRow(7);
        cell = headerRow.createCell(0);
        cell.setCellValue("Hasil");
        cell.setCellStyle(headerCellStyle);

        cell = headerRow.createCell(1);
        cell.setCellValue(hasilteks);
        cell.setCellStyle(cstyle);
    }

    private void fillDataIntoExcel(Hasil hsl) {
        int rownum = 9;
        Row row = sheet.createRow(rownum++);
        CellStyle cstyle = setBodyCellStyle();

        cell = row.createCell(0);
        cell.setCellValue("No");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(1);
        cell.setCellValue("Respon (ms)");
        cell.setCellStyle(headerCellStyle);

        for (int i = 0; i < hsl.getHasilData().length; i++) {
            row = sheet.createRow(rownum++);
            cell = row.createCell(0);
            cell.setCellValue(i + 1);
            cell.setCellStyle(cstyle);

            cell = row.createCell(1);
            cell.setCellValue(hsl.getHasilData()[i]);
            cell.setCellStyle(cstyle);
        }
    }

    private boolean storeExcelInStorage(Context context, File file) {
        boolean isSuccess;
        try (FileOutputStream fos = new FileOutputStream(file)) {
            workbook.write(fos);
            Log.d(TAG, "Writing file" + file);
            isSuccess = true;
        } catch (IOException e) {
            Log.e(TAG, "Failed to save file due to IOException: ", e);
            isSuccess = false;
        } catch (Exception e) {
            Log.e(TAG, "Failed to save file due to Exception: ", e);
            isSuccess = false;
        }
        return isSuccess;
    }

    public boolean exportDataToWebsite(Object[] hsls, String url) {
        boolean isSuccessful = false;
        try {
            String jsonData = createJsonFromData(hsls);
            String namadata = ""; // Initialize an empty namadata

            // Extract namadata from the first result (assuming all have the same namadata)
            if (hsls.length > 0 && hsls[0] instanceof Hasil) {
                Hasil firstHasil = (Hasil) hsls[0];
                namadata = firstHasil.getNamadata(); // Get namadata from the Hasil object
            }

            // Modify the request URL or file name here, if needed
            RequestBody body = RequestBody.create(jsonData, MediaType.get("application/json; charset=utf-8"));
            Request request = new Request.Builder()
                    .url(url + "/" + namadata) // Append namadata to the URL for sending
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    isSuccessful = true;
                    Log.d(TAG, "Data sent successfully: " + response.body().string());
                } else {
                    Log.e(TAG, "Failed to send data: " + response.message());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "IOException: ", e);
        }
        return isSuccessful;
    }


    private String createJsonFromData(Object[] hsls) {
        Gson gson = new Gson();
        return gson.toJson(hsls);
    }

    private String formatTestData(Hasil hsl) {
        // Implementation for formatting the test data into a string
        return "";  // Replace with actual implementation
    }

    private String getHasilText(int rataRata) {
        // Implementation for getting result text based on average result
        return "";  // Replace with actual implementation
    }
}
