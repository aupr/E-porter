package app;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LogKeeper {
    public static Logger logger;
    public static String currentLogFile;

    public static void init(String filename) {
        logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        FileHandler fh;

        try {
            // This block configure the logger with handler and formatter
            String currntTimeString = new SimpleDateFormat("yyyy_MM_dd_HH_mm").format(new Date());
            currentLogFile = filename + "_" + currntTimeString + ".log";
            fh = new FileHandler(currentLogFile);
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);

            logger.info("Applicaiton started...");
        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    public static String getLog() {
        String logString = "";
        try {
            File myObj = new File(currentLogFile);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                logString = logString.concat(data).concat(Character.toString((char)10));
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return logString;
    }
}
