package app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WmicCsproduct {
    List<String> commandWord = new ArrayList<>();

    public static String getList() {
        List<String> lines = null;
        try {
            lines = WindwosCommand.execute("wmic csproduct list");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines.toString();
    }

    public static String getUuid() {
        List<String> lines = null;
        try {
            lines = WindwosCommand.execute("wmic csproduct get uuid");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines.get(2);
    }
}
