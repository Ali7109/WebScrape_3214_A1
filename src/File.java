public class File {
    private String name;
    private long size;
    private String url;

    public File () {}

    public long getSize() {
        return size;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
