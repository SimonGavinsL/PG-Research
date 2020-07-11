import java.util.*;
import java.nio.file.*;
import org.json.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class JsonLoader {
    public static String filePath = "src/functions_copy.json";

    private static String readLineByLineJava8(String filePath) {
        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines(Paths.get(filePath), StandardCharsets.UTF_8)) {
            stream.forEach(contentBuilder::append);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contentBuilder.toString();
    }

    public static void load() {
        try {
            String str = readLineByLineJava8(filePath);
            System.out.println(str);

            JSONObject jsonObject = new JSONObject(str);
            System.out.println(jsonObject.keySet());

            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String key = keys.next();

                Inspect inspect = new Inspect(jsonObject.getString(key));
                inspect.cutAST();

                String contentToAppend = "\n" + key + "," + inspect.valid + "," + inspect.valid_total;
                Files.write(
                        Paths.get("src/result.csv"),
                        contentToAppend.getBytes(),
                        StandardOpenOption.APPEND);

                System.out.println(key);
                System.out.println(inspect.valid);
                System.out.println(inspect.valid_total);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}