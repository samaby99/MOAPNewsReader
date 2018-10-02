package at.technikum_wien.sommerbauer.newsreader.data;

import java.io.Serializable;
import java.net.URL;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class NewsItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private String mTitle = "";
    private String mDescription = "";
    private URL mImageURL = null;
    private URL mLink = null;
    private String mUniqueID = "";
    private String mCreator = "";
    private Date mPublicationDate = null;
    private Set<String> mKeywords = new HashSet<>();

    public NewsItem(String title,
                  String description,
                  URL imageURL,
                  URL link,
                  String uniqueID,
                  String creator,
                  Date publicationDate) {
        this.mTitle = title;
        this.mDescription = description;
        this.mImageURL = imageURL;
        this.mUniqueID = uniqueID;
        this.mLink = link;
        this.mCreator = creator;
        this.mPublicationDate = publicationDate;
        this.mKeywords = new HashSet<>();
    }

    public NewsItem() {}

    public URL getImageURL() { return mImageURL; }

    public void setImageURL(URL mImageURL) { this.mImageURL = mImageURL; }

    public URL getLink() { return mLink; }

    public void setLink(URL mLink) { this.mLink = mLink; }

    public String getUniqueID() { return mUniqueID; }

    public void setUniqueID(String mUniqueID) { this.mUniqueID = mUniqueID;  }

    public String getTitle() { return mTitle; }

    public void setTitle(String title) { this.mTitle = title; }

    public String getDescription() { return mDescription; }

    public void setDescription(String description) { this.mDescription = description; }

    public String getCreator() { return mCreator; }

    public void setCreator(String author) { this.mCreator = author; }

    public Date getPublicationDate() { return mPublicationDate; }

    public void setPublicationDate(Date publicationDate) { this.mPublicationDate = publicationDate; }

    public Set<String> getKeywords() { return mKeywords; }

    public void addKeyword(String keyword) { this.mKeywords.add(keyword); }
}
