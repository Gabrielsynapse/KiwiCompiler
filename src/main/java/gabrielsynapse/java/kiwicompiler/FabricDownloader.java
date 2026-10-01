package gabrielsynapse.java.kiwicompiler;

import java.nio.file.Path;

public class FabricDownloader extends ServerDownloader{
    public FabricDownloader(String minecraftVersion,String loaderVersion){
        super("fabric",minecraftVersion,loaderVersion);
    }
    @Override
    public void startDownload(){
        super.startDownload();
        String installerVersion = "1.0.0";

        // URL para baixar o Server JAR pronto do Fabric
        String fabricServerUrl = String.format(
                "https://meta.fabricmc.net/v2/versions/loader/%s/%s/%s/server/jar",
                this.MINECRAFT_VERSION, this.LOADER_VERSION, installerVersion
        );
        this.ROOT.mkdirs();

        Path outputPath = Path.of(this.ROOT.getAbsolutePath(), "server.jar");

        System.out.println("Baixando servidor Fabric de: " + fabricServerUrl);
        downloadFile(fabricServerUrl, outputPath);
    }
}
