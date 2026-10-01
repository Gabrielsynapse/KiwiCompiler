package gabrielsynapse.java.kiwicompiler;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

public class Util {
    public static String getPwd(){
        return System.getProperty("user.dir");
    }
    public static String getJavaBin(){
        return System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
    }
    public static void writeFile(Path path,String string){
        try {
            Files.writeString(path,string);
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
    }
    public static void shellProcess(List<String> command, File directory){
        ProcessBuilder processBuilder = new ProcessBuilder(command).directory(directory);
        try{
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while( (line = reader.readLine()) != null){
                System.out.println(line);
            }
            int exitCode = process.waitFor();
            System.out.println(exitCode);
        }catch(IOException | InterruptedException e){
            PrintSystem.error(e.getMessage());
        }
    }
    public static void runGradle(File directory){
        shellProcess(List.of("./gradlew","build"),directory);
    }
    public static boolean fileExistAndIsFile(File file){
        return file.exists() && file.isFile();
    }
    public static boolean fileExistAndIsDir(File file){
        return file.exists() && file.isDirectory();
    }
    public static void deleteIfExists(Path path){
        try {
            Files.walkFileTree(path, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        }catch(IOException e){
            PrintSystem.error(e.getMessage());
        }
    }
    public static void copyFile(String source,String destination){
        File fileSource = new File(source);
        File fileDestination = new File(destination);

        try {
            if(fileExistAndIsFile(fileSource)){
                if(fileExistAndIsDir(fileDestination)){
                    Path path1 = fileSource.toPath();
                    Path path2 = new File(fileDestination,fileSource.getName()).toPath();
                    System.out.println("path1: " + path1 + " path2: " + path2);
                    Files.copy(path1,path2,StandardCopyOption.REPLACE_EXISTING);
                }else {
                    throw new IOException("fileDestination: " + destination + " nao existe ou nao é um diretorio");
                }
            }else {
                throw new IOException("fileSource: " + source + " nao existe ou nao é um arquivo");
            }
        }catch (IOException e){
            PrintSystem.error("erro: " + e.getMessage());
        }
    }
    public static void copyResourceFile(String resourceFile,File destination) throws IOException{
        if(!resourceFile.startsWith("/")){
            resourceFile = "/" + resourceFile;
        }
        try(InputStream input = Util.class.getResourceAsStream(resourceFile)){
            if(input == null){
                throw new IOException("erro ao carregar recurso " + resourceFile);
            }
            if(!destination.getParentFile().exists()){
                destination.getParentFile().mkdirs();
            }
            String[] split = resourceFile.split("/");
            String name = split[split.length - 1];

            Files.copy(input,new File(destination,name).toPath(),StandardCopyOption.REPLACE_EXISTING);
            System.out.println(resourceFile);
        }catch(IOException e){
            PrintSystem.error("erro ao copiar resource " + resourceFile + " para " + destination.getAbsolutePath() + ":" + e.getMessage());
        }
    }
}
