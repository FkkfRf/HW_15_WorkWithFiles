import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import static Utils.UtilsForDataOfFiles.zipFileNameList;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class FilesInZipTest {
    public static ArrayList<String> fileNameList = new ArrayList<>();
    ClassLoader cl = FilesInZipTest.class.getClassLoader();
    public static String zipPath = "src/test/resources/Files";
    public static String zipFilePath = zipPath + ".zip";
    public static String zipName = "Files.zip";

    @DisplayName("Создание архива Zip в test/resources")
    @BeforeAll
    public static void addFilesToZip() {
        ZipUtil.pack(new File(zipPath), new File(zipFilePath));
        System.out.printf("\n Создан файл " + zipFilePath);
    }

    @DisplayName("Удаление архива Zip из test/resources")
    @AfterAll
    public static void deleteZipFiles() throws IOException {
        Files.deleteIfExists(Paths.get(zipFilePath));
        System.out.printf("\n Удалён файл " + zipFilePath);
    }

    @DisplayName(value = "Проверка состава архива Zip из test/resources")
    @Test
    void listingFilesInZip() throws IOException {
        ArrayList<String> fileNameList = zipFileNameList(zipFilePath);
        System.out.println("\n Состав zip (имя файла, формат): " + fileNameList);
    }

    @DisplayName(value = "Проверка CSV файлов в архиве")
    @Test
    void cvsInZipTest() throws IOException, CsvException {
        ArrayList<String> fileNameList = zipFileNameList(zipFilePath);
        String fileType = ".csv";
        System.out.println("Проверка " + fileType);
        if (fileNameList.contains(fileType) == true) {
            int i = fileNameList.indexOf(fileType);
            String fileName = fileNameList.get(i - 1);
            ZipFile zipFile = new ZipFile(new File(zipFilePath));
            try (InputStream isZip = (cl.getResourceAsStream(zipName));
                 ZipInputStream zipInputStream = new ZipInputStream(isZip)) {
                ZipEntry entry;
                while ((entry = zipInputStream.getNextEntry()) != null) {
                    if (entry.getName().contains(fileName)) {
                        String parseFileName = entry.getName();
                        System.out.printf("\n Проверяемый файл: " + parseFileName);
                        try (InputStream is = zipFile.getInputStream(entry);
                             CSVReader cvs = new CSVReader(new InputStreamReader(is))) {
                            List<String[]> content = cvs.readAll();
                            String[] row = content.get(0);
                            assertThat(row[0]).contains("REMEMBER THESE SHORTCUTS");
                        }
                    }
                }
            }
            zipFile.close();
        } else {
            System.out.println("Файл " + fileType + " в архиве отсутствует");
        }
    }

    @DisplayName(value = "Проверка PDF файлов в архиве")
    @Test
    void pdfInZipTest() throws IOException {
        ArrayList<String> fileNameList = zipFileNameList(zipFilePath);
        String fileType = ".pdf";
        System.out.println("Проверка " + fileType);
        if (fileNameList.contains(fileType) == true) {
            int i = fileNameList.indexOf(fileType);
            String fileName = fileNameList.get(i - 1);
            ZipFile zipFile = new ZipFile(new File(zipFilePath));
            try (InputStream isZip = (cl.getResourceAsStream(zipName));
                 ZipInputStream zipInputStream = new ZipInputStream(isZip)) {
                ZipEntry entry;
                while ((entry = zipInputStream.getNextEntry()) != null) {
                    if (entry.getName().contains(fileName)) {
                        String parseFileName = entry.getName();
                        System.out.printf("\n Проверяемый файл: " + parseFileName);
                        try (InputStream is = zipFile.getInputStream(entry)) {
                            PDF pdf = new PDF(is);
                            assertThat(pdf.text.matches("REMEMBER THESE SHORTCUTS"));
                        }
                    }
                }
            }
            zipFile.close();
        } else {
            System.out.println("Файл " + fileType + " в архиве отсутствует");
        }
    }

    @DisplayName(value = "Проверка XLS файлов в архиве")
    @Test
    void xlsInZipTest() throws IOException {
        ArrayList<String> fileNameList = zipFileNameList(zipFilePath);
        String fileType = ".xls";
        System.out.println("Проверка " + fileType);

        if (fileNameList.contains(fileType) == true) {
            int i = fileNameList.indexOf(fileType);
            String fileName = fileNameList.get(i - 1);
            ZipFile zipFile = new ZipFile(new File(zipFilePath));
            try (InputStream isZip = (cl.getResourceAsStream(zipName));
                 ZipInputStream zipInputStream = new ZipInputStream(isZip)) {
                ZipEntry entry;
                while ((entry = zipInputStream.getNextEntry()) != null) {
                    if (entry.getName().contains(fileName)) {
                        String parseFileName = entry.getName();
                        System.out.printf("\n Проверяемый файл: " + parseFileName);
                        try (InputStream is = zipFile.getInputStream(entry)) {
                            XLS xls = new XLS(is);
                            AssertionsForClassTypes.assertThat(
                                    xls.excel.getSheetAt(0)
                                            .getRow(0)
                                            .getCell(0).getStringCellValue())
                                    .isEqualTo("REMEMBER THESE SHORTCUTS");
                            System.out.printf("|| "+ xls.excel.getSheetAt(0).getRow(0).getCell(0).getStringCellValue());
                        }
                    }
                }
            }
            zipFile.close();
        } else {
            System.out.println("Файл " + fileType + " в архиве отсутствует");
        }
    }

    @DisplayName(value = "Проверка JSON файлf")
    @Test
    void jsonTest() throws Exception {
        File file = new File(zipPath + "/passport.json");
        ObjectMapper objectMapper = new ObjectMapper();
        rf.fkkf.model.Passport passport = objectMapper.readValue(file, rf.fkkf.model.Passport.class);
        Assertions.assertThat(passport.name).isEqualTo("Сергей");
        Assertions.assertThat(passport.surname).isEqualTo("Петров");
        Assertions.assertThat(passport.patronymic).isEqualTo("Васильевич");
        Assertions.assertThat(passport.gender).isEqualTo("муж");
        Assertions.assertThat(passport.okato).isEqualTo(40);
        Assertions.assertThat(passport.issueYear).isEqualTo(59);
        Assertions.assertThat(passport.number).isEqualTo("233765");

    }

}




