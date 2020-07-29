import org.apache.http.HttpEntity;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class Predict {
    public String Payload;
    public String Result = "";

    public Predict(String payload) {
        Payload = payload;
    }

    public void pred() {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost("https://code2vec.ga/predict");
            httpPost.setEntity(new StringEntity(Payload));

            try (CloseableHttpResponse response2 = httpclient.execute(httpPost)) {
                HttpEntity entity2 = response2.getEntity();

                // do something useful with the response body
                // and ensure it is fully consumed
                JSONObject j = new JSONObject(EntityUtils.toString(entity2));
                JSONObject js = (JSONObject) j.get("0");
                JSONArray jso = (JSONArray) js.get("predictions");
                JSONObject json = jso.getJSONObject(0);
                JSONArray jsonn = (JSONArray) json.get("name");
                for (int i = 0; i < jsonn.length(); ++i) {
                    String NameSec = jsonn.getString(i);
                    if (i > 0) {
                        NameSec = NameSec.substring(0, 1).toUpperCase() + NameSec.substring(1);
                    }
                    Result = Result.concat(NameSec);
                }

                EntityUtils.consume(entity2);
            } catch (HttpResponseException e) {
                Result = "null";
            }
        } catch (Exception e) {
            Result = "null";
        }
    }

}