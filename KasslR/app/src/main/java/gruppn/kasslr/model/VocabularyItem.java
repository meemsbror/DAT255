package gruppn.kasslr.model;

import org.json.JSONException;
import org.json.JSONObject;

public class VocabularyItem {
    private int id;
    private String name;
    private String image;
    private long lastModified;
    private boolean mine = true;

    public VocabularyItem(String name, String imageName) {
        this.name = name;
        this.image = imageName;
    }

    public VocabularyItem(String name, String imageName, int id, boolean mine) {
        this(name, imageName);
        this.id = id;
        this.mine = mine;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name.trim();
    }

    /**
     * @return The name of the image without file extension
     */
    public String getImageName() {
        return image;
    }

    public void setImageName(String image) {
        this.image = image;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    public boolean isMine() {
        return mine;
    }

    public void setMine(boolean mine) {
        this.mine = mine;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VocabularyItem)) return false;

        VocabularyItem that = (VocabularyItem) o;

        if (getId() != that.getId()) return false;
        if (isMine() != that.isMine()) return false;
        if (getName() != null ? !getName().equals(that.getName()) : that.getName() != null)
            return false;
        return image != null ? image.equals(that.image) : that.image == null;

    }

    @Override
    public int hashCode() {
        int result = getId();
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (image != null ? image.hashCode() : 0);
        result = 31 * result + (int) (getLastModified() ^ (getLastModified() >>> 32));
        result = 31 * result + (isMine() ? 1 : 0);
        return result;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject item = new JSONObject();
        item.put("Word", name);
        item.put("Picture", image);

        return item;
    }

    @Override
    public String toString() {
        return "VocabularyItem{"
                + "name=" + name
                + ",image=" + image
                + ",mine=" + mine
                + "}";
    }


}
