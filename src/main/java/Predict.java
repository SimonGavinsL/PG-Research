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
    public String Result;
    public boolean isValid = true;

    public Predict(String payload) {
        Payload = payload;
    }

    public void pred() throws Exception {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost("https://code2vec.ga/predict");
            httpPost.setEntity(new StringEntity(Payload));

            try (CloseableHttpResponse response2 = httpclient.execute(httpPost)) {
                System.out.println(response2.getStatusLine());
                HttpEntity entity2 = response2.getEntity();

                // do something useful with the response body
                // and ensure it is fully consumed
                JSONObject j = new JSONObject(EntityUtils.toString(entity2));
                JSONObject js = (JSONObject) j.get("0");
                JSONArray jso = (JSONArray) js.get("predictions");
                JSONObject json = jso.getJSONObject(0);

                // TODO: need to convert to plain string
                Result = json.get("name").toString();

                EntityUtils.consume(entity2);
            } catch (HttpResponseException e) {
                isValid = false;
            }
        }
    }

}