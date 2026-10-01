package gabrielsynapse.java.kiwicompiler;

import gabrielsynapse.java.kiwicompiler.exception.SettingsGradleNotFoundException;

import java.io.File;
import java.util.Properties;

public class Mod {
    public static class ModProperties{
        public final String baseName;
        public final String baseVersion;
        public ModProperties(Properties properties){
            this.baseName = properties.getProperty("archives_base_name");
            this.baseVersion = properties.getProperty("mod_version");
        }
        //metodos getters
        public String getNameJar(){
            return this.baseName + "-" + this.baseVersion;
        }
    }
    public final ModProperties properties;
    public final Environment environment;
    public final String PATH;

    public Mod(String name,Environment environment){
        String pwd = Util.getPwd();

        File gradleProperties = new File(pwd + "/" + name + "/gradle.properties");
        //verificando se o arquivo nao existe ou se nao é um arquivo
        if(!gradleProperties.exists() || !gradleProperties.isFile()){
            throw new SettingsGradleNotFoundException("erro ao ler gradle em " + gradleProperties.getParentFile());
        }
        this.environment = environment;
        this.properties = new ModProperties(Global.getProperties(gradleProperties.toPath()));
        this.PATH = gradleProperties.getParentFile().getAbsoluteFile().getPath();
    }
}
