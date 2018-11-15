package livefyre.models;

/**
 * Created by Adobe Systems Incorporated on 07/04/15.
 */
public class Attachments {
    private String url;
    private String link;
    private String provider_name;
    private String thumbnail_url;
    private String type;
    private int width;
    private int height;
    private String HTML;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getProvider_name() {
        return provider_name;
    }

    public void setProvider_name(String provider_name) {
        this.provider_name = provider_name;
    }

    public String getThumbnail_url() {
        return thumbnail_url;
    }

    public void setThumbnail_url(String thumbnail_url) {
        this.thumbnail_url = thumbnail_url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getHTML() {
        return HTML;
    }

    public void setHTML(String HTML) {
        this.HTML = HTML;
    }
}
