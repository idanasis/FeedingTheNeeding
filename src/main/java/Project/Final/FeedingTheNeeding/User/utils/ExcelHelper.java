package Project.Final.FeedingTheNeeding.User.utils;

import Project.Final.FeedingTheNeeding.Authentication.DTO.NeedyRegistrationRequest;
import org.apache.poi.ss.usermodel.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;

public class ExcelHelper {

    private static final Set<String> ALLOWED_STREETS = new HashSet<>(Arrays.asList(
            "העיר העתיקה", "נווה עופר", "המרכז האזרחי", "א'", "ב'", "ג'", "ד'", "ה'",
            "ו'", "ט'", "י\"א", "נאות לון", "נווה זאב", "נווה נוי", "נחל בקע",
            "נחל עשן (נווה מנחם)", "רמות", "נאות אברהם (פלח 6)", "נווה אילן (פלח 7)",
            "הכלניות", "סיגליות", "פארק הנחל"
    ));

    public static List<NeedyRegistrationRequest> parseExcel(MultipartFile file) {
        List<NeedyRegistrationRequest> list = new ArrayList<>();

        try (InputStream is = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            boolean skipHeader = true;

            for (Row row : sheet) {
                if (skipHeader) {
                    skipHeader = false;
                    continue;
                }

                String street = row.getCell(2).getStringCellValue().trim();

                // Skip row if the street is not allowed
                if (!ALLOWED_STREETS.contains(street)) {
                    System.out.println("Skipping row due to invalid street: " + street);
                    continue;
                }

                NeedyRegistrationRequest needy = NeedyRegistrationRequest.builder()
                        .firstName(row.getCell(0).getStringCellValue())
                        .lastName(row.getCell(1).getStringCellValue())
                        .street(street)
                        .address(row.getCell(3).getStringCellValue())
                        .phoneNumber(row.getCell(4).getCellType() == CellType.NUMERIC ?
                                String.valueOf((long) row.getCell(4).getNumericCellValue()) :
                                row.getCell(4).getStringCellValue())
                        .familySize((int) row.getCell(5).getNumericCellValue())
                        .build();

                list.add(needy);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
