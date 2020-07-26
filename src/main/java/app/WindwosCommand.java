package app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WindwosCommand {
    public static List<String> execute(String commandLineToExecute) throws IOException {
        String[] commandWords = commandLineToExecute.split("\\s");
        List<String> commandWordsList = Arrays.asList(commandWords);

        ProcessBuilder processBuilder = new ProcessBuilder(commandWordsList);
        Process process = processBuilder.start();

        BufferedReader standardInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader errorInput = new BufferedReader(new InputStreamReader(process.getErrorStream()));

        List<String> inputList = new ArrayList<>();
        String line;
        while ((line = standardInput.readLine()) != null)
            inputList.add(line);
        while ((line = errorInput.readLine()) != null)
            inputList.add(line);
        return inputList;
    }
}
