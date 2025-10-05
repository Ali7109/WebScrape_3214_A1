import java.net.URI;
import java.net.http.HttpResponse;
import java.util.List;

public class WebSizeScraper {

    private final URI uri;

    public WebSizeScraper(URI uri) {
        this.uri = uri;
    }

    void scan(){

        System.out.println("Scanning " + this.uri);

        try {

            // Initialize httpHelper to setup connection
            HttpHelper httpHelper = new HttpHelper();
            String urlString = uri.toURL().toString();

            // Send request and retrieve response
            HttpResponse<String> response = httpHelper.sendRequestForBody(this.uri);
            String htmlContent = response.body();

            List<File> fileObjs = FileHelper.extractAllMediaSizes(htmlContent, urlString);

            long totalFileSizes = 0;

            for(File file: fileObjs) {
                long fileSize = file.getSize();
                System.out.println(file.getName() + " " + fileSize + " bytes");
                totalFileSizes += fileSize;
            }

            System.out.println("Total: " + totalFileSizes + " bytes");

        } catch (Exception e) {
            System.err.println("[WebSizeScraper.scan]: " + e.getMessage());
        }
    }
}
