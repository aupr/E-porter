package app;

import java.io.*;
import java.util.*;

public class Settings {
    protected Encryptor encryptor = new Encryptor("String to initialize - xylowuejikel45s_");
    private String filename;
    private Properties properties;

    public Settings(String filename) {
        this.filename = filename;
        this.properties = new Properties();

        try {
            InputStream inputStream = new FileInputStream(filename);
            this.properties.loadFromXML(inputStream);
            inputStream.close();
        } catch (IOException e) {
            this.store();
        }
    }

    public void store() {
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(this.filename);
            this.properties.storeToXML(outputStream, "E-mail reporter setting file | do not edit manually if you are not an expert");
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void set(String key, String value) {
        properties.setProperty(key, value);
    }

    public void clear() {
        properties.clear();
    }

    public void setEncrypt(String key, String value) {
        this.set(key, this.encryptor.encrypt(value));
    }

    public String get(String key) {
        return this.properties.getProperty(key);
    }

    public String get(String key, String defaultValue) {
        return this.properties.getProperty(key, defaultValue);
    }

    public String getDecrypt(String key) {
        return this.encryptor.decrypt(this.get(key));
    }

    public String getDecrypt(String key, String defaultValue) {
        return this.encryptor.decrypt(this.get(key, defaultValue));
    }

    public Map<String, String> getAll() {
        Map<String, String> map = new LinkedHashMap<>();

        for (String key: properties.stringPropertyNames()) {
            map.put(key, this.get(key));
        }
        return map;
    }

}
