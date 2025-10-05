import java.net.URI;

public static void main(String[] args) throws Exception {
    if (args.length != 1) {
        System.err.println("Usage: java Assignment1 <url>");
        System.exit(1);
    }

    try {
        URI uri = new URI(args[0]);

        // Validate that it's actually a URL (has scheme and authority)
        if (uri.getScheme() == null || uri.getHost() == null) {
            throw new IllegalArgumentException("Not a valid URL");
        }

        // Initialize the helper class for scraping size logic
        WebSizeScraper scraper = new WebSizeScraper(uri);

        scraper.scan();

        System.out.println("press CTRL+C to exit.");

        while (true) {
            Thread.sleep(1000);
        }
    } catch (Exception e) {

        System.err.println("❌ Invalid URL: " + args[0]);
        System.exit(1);

    }
}