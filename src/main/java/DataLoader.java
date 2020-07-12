import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;

public class DataLoader {
    public static void load() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("src/data/functions_copy.json"));
            String thisLine;

            while ((thisLine = br.readLine()) != null) {
                int len = thisLine.length();

                // get rid of meaningless lines
                if (len > 5) {
                    JSONObject jsonObject = new JSONObject("{" + thisLine + "}");
                    System.out.println(jsonObject);

                    try {
                        FileWriter myWriter = new FileWriter("src/data/result.csv");
                        myWriter.write(
                                "Key,OriginalName,OriginalPrediction,UnequalToName,UnequalToPrediction,Total");
                        myWriter.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Iterator<String> keys = jsonObject.keys();
                    while (keys.hasNext()) {
                        String key = keys.next();

                        Inspect inspect = new Inspect(jsonObject.getString(key));
                        inspect.ASTMutate();

                        String contentToAppend = "\n" + key + "," + inspect.Original + "," +
                                inspect.Original_Prediction + "," + inspect.UnequalToName + "," +
                                inspect.UnequalToPrediction + "," + inspect.Total;
                        Files.write(
                                Paths.get("src/data/result.csv"),
                                contentToAppend.getBytes(),
                                StandardOpenOption.APPEND);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}