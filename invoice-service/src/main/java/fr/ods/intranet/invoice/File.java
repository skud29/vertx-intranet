package fr.ods.intranet.invoice;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

@DataObject(generateConverter=true)
public class File {
    private String filename;
    private String path;
    private int size;

    public File() {
    }

    public File(JsonObject json) {
        // A converter is generated to easy the conversion from and to JSON.
        FileConverter.fromJson(json, this);
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        FileConverter.toJson(this, json);
        return json;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        File file = (File) o;

        return path.equals(file.getPath()) && filename.equals(file.getFilename());
    }

    @Override
    public int hashCode() {
        int result = path.hashCode();
        result = 31 * result + filename.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return this.toJson().encodePrettily();
    }
}
