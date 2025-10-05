import java.net.URI;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileHelper {

    static HttpHelper httpHelper = new HttpHelper();

    private FileHelper(){}

    private static long getFileSizeFromHeader(HttpResponse<Void> response){
        try {
            return response.headers()
                    .firstValueAsLong("Content-Length")
                    .orElse(-1L);

        } catch (Exception e) {
            System.out.println("[FileHelper.getFileSizeFromHeader]: " + e.getMessage());
            return -1L;
        }
    }

    public static List<File> extractAllMediaSizes(String htmlContent, String baseUrl){

        List<File> files = new ArrayList<>();

        // Extract and store different media types
        List<String> urls = new ArrayList<>();

        // Add base url first
        urls.add(baseUrl);

        // Extract different media types
        urls.addAll(extractUrls(htmlContent, "<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"]"));
        urls.addAll(extractUrls(htmlContent, "<video[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"]"));
        urls.addAll(extractUrls(htmlContent, "<source[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"]"));
        urls.addAll(extractUrls(htmlContent, "<audio[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"]"));

         urls.addAll(extractUrls(htmlContent, "<link[^>]+href\\s*=\\s*['\"]([^'\"]+\\.css[^'\"]*)['\"]"));
         urls.addAll(extractUrls(htmlContent, "<script[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"]"));

        for (String url: urls) {

            try {
                // Convert to absolute URL - I had trouble using base urls so heres a helper method handling edge cases
                String fullUrl = makeAbsoluteUrl(url, baseUrl);

                // DEBUG: Uncomment to see converted URLs
                // System.out.println("Original: " + url);
                // System.out.println("Full URL: " + fullUrl);
                // System.out.println("---");

                // Create a file object
                File file = new File();
                file.setUrl(fullUrl);
                file.setName(extractFileName(url));

                // Get the File Size from header request using url
                HttpResponse<Void> response = httpHelper.sendRequestForHeader(URI.create(fullUrl));
                long fileSize = getFileSizeFromHeader(response);

                file.setSize(fileSize);

                files.add(file);

            } catch (Exception e) {
                System.err.println("[FileHelper.extractAllMediaSizes] Error processing URL '" + url + "': " + e.getMessage());
            }
        }

        return files;
    }

    private static String makeAbsoluteUrl(String url, String baseUrl) {
        // Already absolute
        if (url.startsWith("http://") || url.startsWith("https://")) {
            return url;
        }

        // Protocol-relative URL (starts with //)
        if (url.startsWith("//")) {
            return "https:" + url;
        }

        // Extract the domain from baseUrl
        String domain;
        try {
            URI baseUri = new URI(baseUrl);
            domain = baseUri.getScheme() + "://" + baseUri.getHost();

            // Handle port if present
            if (baseUri.getPort() != -1) {
                domain += ":" + baseUri.getPort();
            }
        } catch (Exception e) {
            System.err.println("[FileHelper.makeAbsoluteUrl] Error parsing baseUrl: " + e.getMessage());
            // Fallback: use baseUrl as-is
            domain = baseUrl;
        }

        // Remove trailing slash from domain
        if (domain.endsWith("/")) {
            domain = domain.substring(0, domain.length() - 1);
        }

        // Absolute path (starts with /)
        if (url.startsWith("/")) {
            return domain + url;
        }

        // Relative path (no leading /)
        // Need to handle this better - should be relative to the current page path, not just domain
        // For now, keeping your simple approach
        return domain + "/" + url;
    }

    private static List<String> extractUrls(String htmlContent, String regexForFileType) {
        List<String> urls = new ArrayList<>();
        Pattern pattern = Pattern.compile(regexForFileType, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(htmlContent);

        while(matcher.find()){
            String url = matcher.group(1);
            // Skip data URLs, javascript, and anchors and empty edge cases
            if (!url.startsWith("data:") &&
                    !url.startsWith("javascript:") &&
                    !url.startsWith("#") &&
                    !url.trim().isEmpty()){
                urls.add(url);
            }
        }

        return urls;
    }

    private static String extractFileName(String url) {
        // Remove query parameters and fragments
        int queryIdx = url.indexOf('?');
        if (queryIdx != -1) {
            url = url.substring(0, queryIdx);
        }

        int frgIdx = url.indexOf('#');
        if (frgIdx != -1) {
            url = url.substring(0, frgIdx);
        }

        // Extract filename after last slash
        int slashIdx = url.lastIndexOf('/');
        if (slashIdx != -1 && slashIdx < url.length() - 1) {
            String filename = url.substring(slashIdx + 1);
            // Return filename or fallback if empty
            return filename.isEmpty() ? "index.html" : filename;
        }

        // If no slash, return the whole URL (or a default)
        return url.isEmpty() ? "index.html" : url;
    }
}