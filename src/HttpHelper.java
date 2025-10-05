import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpHelper {

    private final HttpClient client;
    private final String requestHeaderAgent;
    public HttpHelper (){
        this.client = HttpClient.newHttpClient();
        this.requestHeaderAgent = "University Assignment (for educational purposes only)";
    }

    HttpResponse<String> sendRequestForBody(URI uri){
        try {

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .GET()
                    .header("User-Agent", this.requestHeaderAgent)
                    .build();

            return this.client.send(request,
                    HttpResponse.BodyHandlers.ofString());

        } catch (Exception e) {
            System.err.println("[HttpHelper.sendRequestForBody]: " + e.getMessage());
            return null;
        }
    }

    HttpResponse<Void> sendRequestForHeader(URI uri){
        try {

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .method("HEAD", HttpRequest.BodyPublishers.noBody())
                    .header("User-Agent", this.requestHeaderAgent)
                    .build();
            return this.client.send(request,
                    HttpResponse.BodyHandlers.discarding());

        } catch (Exception e) {
            System.err.println("[HttpHelper.sendRequestForHeader]: " + e.getMessage());
            return null;
        }
    }


}
