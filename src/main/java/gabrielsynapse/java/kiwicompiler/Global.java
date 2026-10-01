package gabrielsynapse.java.kiwicompiler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Properties;

public class Global {
    private static KiwiConfig kiwiConfig;
    public static final String ROOT        = System.getProperty("user.home") + "/KiwiCompiler";
    public static final String ROOT_SERVER = ROOT + "/server";
    public static final String ROOT_CLIENT = ROOT + "/client";

    //metodos getters
    public static KiwiConfig loadConfig(){
        if(kiwiConfig == null) {
            Global.kiwiConfig = KiwiConfig.load();
        }
        return Global.kiwiConfig;
    }
    public static KiwiConfig getConfig(){
        return kiwiConfig;
    }
    public static Properties getProperties(Path path){
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream(path.toFile())) {
            properties.load(input);
        }catch (IOException e){
            PrintSystem.error(e.getMessage());
        }
        return properties;
    }
}
