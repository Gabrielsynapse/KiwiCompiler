package gabrielsynapse.java.kiwicompiler;

import gabrielsynapse.java.kiwicompiler.exception.SettingsGradleNotFoundException;

import java.io.File;
import java.nio.file.Files;

public class Main {
    public static void init(){
        //criar o arquivo de config se nao existir
        KiwiConfig kiwiConfig = new KiwiConfig("","", LoaderType.FABRIC,false);

        if(KiwiConfig.FILE.exists()) {
            PrintSystem.info("arquivo '%s' ja existe",KiwiConfig.FILE.getName());
            return;
        }
            kiwiConfig.save();
    }
    public static void build(){
        String pathKiwi = KiwiConfig.FILE.getName();
        String pwd = Util.getPwd();

        if(!KiwiConfig.FILE.exists()){
            PrintSystem.info("O arquivo '%s' nao existe",pathKiwi);
            return;
        }
        KiwiConfig config = Global.loadConfig();
        PrintSystem.info("arquivo '%s' carregado!",pathKiwi);

        //verificando se o arquivo gradle.properties existe no pwd
        File settingsGradleFile = new File(pwd + "/gradle.properties");
        if(!settingsGradleFile.exists()){
            throw new SettingsGradleNotFoundException("Falha ao ler arquivo raiz");
        }
        //build
        //verificando se o projeto é modularizado
        if(config.multimodule){
            //projeto modular
            PrintSystem.info("compilando mods em '%s'",pwd);
            Util.runGradle(new File(pwd));
        }else {
            //projeto individual
            for(Mod mod: config.getMods()){
                PrintSystem.info("compilando mod '%s' em '%s'",mod.properties.baseName,mod.PATH);
                Util.runGradle(new File(mod.PATH));
            }
        }
        PrintSystem.info("projeto compilado!");
    }
    public static void start(){
        PrintSystem.info("preparando ambiente do servidor");
        KiwiConfig config = Global.loadConfig();
        FabricDownloader fabricDownloader = new FabricDownloader(config.minecraftVersion,config.loaderVersion);
        Client client = new Client(config.loader,config.minecraftVersion,config.loaderVersion);

        if(fabricDownloader.isNotExist()){
            fabricDownloader.startDownload();
        }
        fabricDownloader.prepare();
        System.out.println(KiwiConfig.GSON.toJson(config));
        fabricDownloader.deleteMods();

        for(Mod mod:config.getMods()){
            File modFile = new File(mod.PATH,"build/libs/" + mod.properties.getNameJar() + ".jar");
            if(!modFile.exists()){
                PrintSystem.error("Erro ao tentar copiar mod %s mod nao compilado",mod.properties.baseName);
                continue;
            }
            if(mod.environment.equals(Environment.SERVER)) {
                fabricDownloader.prepareMod(mod, modFile);
            }else if(mod.environment.equals(Environment.BOTH)){
                fabricDownloader.prepareMod(mod,modFile);
                client.prepareMod(mod,modFile);
            }else if(mod.environment.equals(Environment.CLIENT)){
                client.prepareMod(mod,modFile);
            }
        }
        //preparando mods de predefinicao do servidor
        File[] prepareServer = config.getFilesPrepare(Environment.SERVER);
        File[] prepareClient = config.getFilesPrepare(Environment.CLIENT);
        File[] prepareBoth   = config.getFilesPrepare(Environment.BOTH);

        for(File filePrepareServer:prepareServer){
            fabricDownloader.copyModPrepare(filePrepareServer);
        }
        for(File filePrepareClient:prepareClient){
            client.copyModPrepare(filePrepareClient);
        }
        for(File filePrepareBoth:prepareBoth){
            fabricDownloader.copyModPrepare(filePrepareBoth);
            client.copyModPrepare(filePrepareBoth);
        }
        fabricDownloader.runServer();

        PrintSystem.info("preparando ambiente do client");
        client.runClient();
    }
    public static void main(String[] args) {
        for(String arg:args){
            if(arg.equals("init")){
                init();
            }else if(arg.equals("build")){
                build();
            }else if(arg.equals("start")){
                start();
            }
        }
    }
}
