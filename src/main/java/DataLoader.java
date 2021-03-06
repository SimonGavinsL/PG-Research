import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;

public class DataLoader {
    Path Result_File = Paths.get("src/data/result3.csv");
    Path Source_File = Paths.get("src/data/functions.json");

    public void load() {
        try {
            Files.write(
                    Result_File,
                    "Key,OriginalName,OriginalPrediction,UnequalToName,UnequalToPrediction,Total".getBytes(),
                    StandardOpenOption.CREATE);

            try {
                Files.lines(Source_File)
                        .filter(line -> line.length() > 5)
                        .forEach(line -> {
                            JSONObject jsonObject = new JSONObject("{" + line + "}");
                            System.out.println(jsonObject);

                            Iterator<String> keys = jsonObject.keys();
                            while (keys.hasNext()) {
                                String key = keys.next();

                                Inspect inspect = new Inspect(key, jsonObject.getString(key));
                                try {
                                    inspect.ASTMutate();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                String contentToAppend = "\n" + key + "," + inspect.Original + "," +
                                        inspect.Original_Prediction + "," + inspect.UnequalToName + "," +
                                        inspect.UnequalToPrediction + "," + inspect.Total;
                                try {
                                    Files.write(
                                            Result_File,
                                            contentToAppend.getBytes(),
                                            StandardOpenOption.APPEND);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}