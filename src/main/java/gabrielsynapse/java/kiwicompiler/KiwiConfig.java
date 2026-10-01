package gabrielsynapse.java.kiwicompiler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KiwiConfig {
    public String minecraftVersion;
    public String loaderVersion;
    public LoaderType loader;
    public boolean multimodule;
    public String prepareClient;
    public String prepareServer;
    public String prepareBoth;

    public Map<String,Environment> mods = new HashMap<>();

    private KiwiConfig(){

    }
    public KiwiConfig(String minecraftVersion,String loaderVersion,LoaderType loader,boolean multimodule){
        this.minecraftVersion = minecraftVersion;
        this.loaderVersion = loaderVersion;
        this.loader = loader;
        this.multimodule = multimodule;
        this.prepareClient = null;
        this.prepareServer = null;
        this.prepareBoth   = null;
    }

    //metodos getters
    private File getFilePrepare(String prepare){
        File path = prepare != null ? new File(Util.getPwd(),prepare) : null;
        return path != null ? (path.exists() ? path : null) : null;
    }
    public File getFilePrepare(Environment environment){
        switch(environment){
            case BOTH -> {
                return getFilePrepare(this.prepareBoth);
            }
            case SERVER -> {
                return getFilePrepare(this.prepareServer);
            }
            case CLIENT -> {
                return getFilePrepare(this.prepareClient);
            }
            default -> {
                return null;
            }
        }
    }
    public File[] getFilesPrepare(Environment environment){
        File path = this.getFilePrepare(environment);
        return path != null ? path.listFiles() : new File[]{};
    }
    public Mod getMod(String name){
        return new Mod(name,this.mods.getOrDefault(name,Environment.SERVER));
    }
    public Mod[] getMods(){
        List<Mod> mods = new ArrayList<>();

        for(String modName:this.mods.keySet()){
            Environment environment = this.mods.get(modName);
            mods.add(getMod(modName));
        }
        return mods.toArray(new Mod[this.mods.size()]);
    }
    //metodos setters
    public void save(){
        String jsonString = GSON.toJson(this);
        System.out.println(jsonString);
        Util.writeFile(FILE.toPath(),jsonString);
    }
    //metodos estaticos setters
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final File FILE = new File(Util.getPwd()+"/kiwi.json");
    public static File getRootServer(String minecraftVersion,String loaderVersion,String loader){
        return new File(Global.ROOT_SERVER,String.format("%s-%s/%s",minecraftVersion,loaderVersion,loader.toLowerCase()));
    }
    public static KiwiConfig load(){
        String jsonString = getFileString();
        return GSON.fromJson(jsonString,KiwiConfig.class);
    }

    public static String getFileString(){
        try {
            return Files.readString(FILE.toPath());
        }catch (IOException e){
            System.out.println("o arquivo de config nao foi encontrado!");
        }
        return "{}";
    }
}
