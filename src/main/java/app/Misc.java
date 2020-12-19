package app;

import java.net.ServerSocket;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Misc {
    public static boolean sendTestMail = false;
    public static boolean isValidLicense = false;
    public static ServerSocket serverSocket;

    public static String patternsToDateTime(String stringWithDateFormatPatterns , long unixTimestampMills) {

        Pattern pattern = Pattern.compile("\\{\\{[^\\}\\{]*\\}\\}");
        Matcher m = pattern.matcher(stringWithDateFormatPatterns);

        while (m.find( )) {
            String datepatternString = m.group(0).replace("{", "").replace("}", "");

            List<String> spa = Arrays.asList(datepatternString.split("@"));
            long backTimeMills = 0;
            if (spa.size()>1) {
                backTimeMills = Long.parseLong(spa.get(1)) * 60000;
            }

            String timedateStringM= new SimpleDateFormat(spa.get(0)).format(new Date(unixTimestampMills - backTimeMills));
            stringWithDateFormatPatterns=  stringWithDateFormatPatterns.replace(m.group(0),timedateStringM );
        }
        return stringWithDateFormatPatterns;
    }
}
