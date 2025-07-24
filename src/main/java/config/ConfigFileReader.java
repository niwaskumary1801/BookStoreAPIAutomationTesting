package config;

import java.io.FileInputStream;
import java.util.Properties;

public class ConfigFileReader 
{
	private static Properties propread = new Properties();

    static {
        try {
            FileInputStream fis = new FileInputStream("ApplicationConfig.properties");
            propread.load(fis);
        } catch (Exception e) {
            throw new RuntimeException("Unable to load ApplicationConfig.properties", e);
        }
    }

    public static String get(String key) {
        return propread.getProperty(key);
    }
}
