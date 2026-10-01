package gabrielsynapse.java.kiwicompiler;

import java.io.File;
import java.util.List;

public class Client extends Thread implements Runnable {
    public final LoaderType loader;
    public final String minecraftVersion;
    public final String loaderVersion;
    public final File PATH;
    public Client(LoaderType loader,String minecraftVersion,String loaderVersion){
        this.loader = loader;
        this.minecraftVersion = minecraftVersion;
        this.loaderVersion = loaderVersion;
        this.PATH = new File(Global.ROOT_CLIENT,minecraftVersion + "-" + loaderVersion + "/" + loader.name().toLowerCase());
    }
    public void preparePath(){
        if(this.PATH.exists())return;
        if(this.PATH.mkdirs()){
        }else {
            PrintSystem.error("erro ao tentar criar diretorio '%s'",this.PATH.getAbsolutePath());
        }
    }
    public void sleepClient(long millis){
        try {
            Thread.sleep(millis);
        }catch (InterruptedException e){
            PrintSystem.error(e.getMessage());
        }
    }
    @Override
    public void run(){
        this.sleepClient(6000);
        //portablemc --main-dir {client_path} start fabric:{mc_version}:{loader_version} -u Gabrielsynapse -s localhost -p 10020
        Util.shellProcess(
            List.of(
                "portablemc","--main-dir",this.PATH.getAbsolutePath(),"start",
                this.loader.name().toLowerCase() + ":" + this.minecraftVersion + ":" + this.loaderVersion,
                "-u","Gabrielsynapse","-s","localhost","-p","10020"
            ),this.PATH
        );
    }
    public void copyModPrepare(File fileMod){
        this.preparePath();
        File path_mods = new File(this.PATH,"mods");
        path_mods.mkdirs();
        Util.copyFile(fileMod.getAbsolutePath(),path_mods.getAbsolutePath());
    }
    public void prepareMod(Mod mod,File path){
        File destination = new File(this.PATH,"mods");
        destination.mkdirs();
        Util.copyFile(path.getAbsolutePath(),destination.getPath());
    }
    public void runClient(){
        this.start();
    }
}
