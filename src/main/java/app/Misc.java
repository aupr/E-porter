package app;

import java.net.ServerSocket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Misc {
    public static boolean sendTestMail = false;
    public static boolean isValidLicense = false;
    public static ServerSocket serverSocket;

    public static String patternsToDateTime(String stringWithDateFormatPatterns , long unixTimestampMills) {

        Pattern pattern = Pattern.compile("\\{\\{[a-z A-Z:]*\\}\\}");
        Matcher m = pattern.matcher(stringWithDateFormatPatterns);

        while (m.find( )) {
            String datepatternString = m.group(0).replace("{", "").replace("}", "");
            String timedateStringM= new SimpleDateFormat(datepatternString).format(new Date(unixTimestampMills));
            stringWithDateFormatPatterns=  stringWithDateFormatPatterns.replace(m.group(0),timedateStringM );
        }
        return stringWithDateFormatPatterns;
    }
}
