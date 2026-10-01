package gabrielsynapse.java.kiwicompiler;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public abstract class ServerDownloader extends Thread implements Runnable{
    private final HttpClient CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();
    public final String LOADER;
    public final String MINECRAFT_VERSION;
    public final String LOADER_VERSION;
    public final File ROOT;
    public final File ROOT_MODS;
    public final File EXECUTABLE_JARFILE;
    public final File EXECUTABLE_RUNSHELL;
    public final File SERVER_PROPERTIES;

    public ServerDownloader(String loader,String minecraftVersion,String loaderVersion){
        this.LOADER            = loader;
        this.MINECRAFT_VERSION = minecraftVersion;
        this.LOADER_VERSION    = loaderVersion;
        this.ROOT = KiwiConfig.getRootServer(this.MINECRAFT_VERSION,this.LOADER_VERSION,this.LOADER);
        this.ROOT_MODS = new File(this.ROOT,"mods");
        this.EXECUTABLE_JARFILE = new File(this.ROOT,"server.jar");
        this.EXECUTABLE_RUNSHELL = new File(this.ROOT,"run.sh");
        this.SERVER_PROPERTIES = new File(this.ROOT,"server.properties");
    }
    @Override
    public void run(){
        //gnome-terminal -- bash -c \"./run.sh;exec bash\"",check=True,shell=True,text=True,cwd=server_path
        Util.shellProcess(
            List.of(
                "chmod","+x","./run.sh"
            ),this.ROOT
        );
        sleepServer(1000);
        String java = "/home/linuxmint/.sdkman/candidates/java/25.0.4.r25-nik/bin/java";

        System.out.println("executando: " + java);
        Util.shellProcess(
            List.of(
                "gnome-terminal","--","bash","-c",java + " -jar ./server.jar nogui;exec bash"
            ),this.ROOT
        );
    }
    public void sleepServer(long millis){
        try {
            Thread.sleep(millis);
        }catch(InterruptedException e){
            PrintSystem.error(e.getMessage());
        }
    }
    public boolean isNotExist(){
        return !( this.ROOT.exists() || this.EXECUTABLE_JARFILE.exists() );
    }
    /**
     * Baixa um arquivo de uma URL e salva no caminho especificado.
     */
    public boolean downloadFile(String url, Path targetPath) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "kiwiCompiler/1.0")
                    .GET()
                    .build();

            // Salva diretamente o fluxo de entrada no arquivo de destino
            HttpResponse<Path> response = CLIENT.send(
                    request,
                    HttpResponse.BodyHandlers.ofFile(targetPath)
            );

            if (response.statusCode() == 200) {
                System.out.println("Download concluído: " + targetPath.getFileName());
                return true;
            } else {
                System.err.println("Falha no download. Código HTTP: " + response.statusCode());
                return false;
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("Erro durante o download: " + e.getMessage());
            return false;
        }
    }

    public void startDownload() {
    }
    public void deleteMods(){
        if(this.ROOT_MODS.exists()){
            for(File file: Objects.requireNonNull(this.ROOT_MODS.listFiles())){
                if(file.isFile()) {
                    Util.deleteIfExists(file.toPath());
                }
            }
        }
    }
    public void copyModPrepare(File modPrepare){
        Util.copyFile(modPrepare.getAbsolutePath(),this.ROOT_MODS.getAbsolutePath());
    }
    public void prepareMod(Mod mod,File path){
        File destination = new File(this.ROOT,"mods");
        destination.mkdirs();
        Util.copyFile(path.getAbsolutePath(),destination.getPath());
    }
    public void prepare(){
        try {
            String loader = this.LOADER.toLowerCase();
            Util.copyResourceFile("server/" + loader + "/server.properties", this.ROOT);
            Util.copyResourceFile("server/" + loader + "/run.sh", this.ROOT);
            Util.copyResourceFile("server/" + loader + "/eula.txt", this.ROOT);
            Util.copyResourceFile("server/" + loader + "/ops.json", this.ROOT);
        }catch (IOException e){
            PrintSystem.error(e.getMessage());
        }
    }

    public void runServer() {
        this.start();
    }
}