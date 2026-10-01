package gabrielsynapse.java.kiwicompiler;

public class PrintSystem {
    public enum Level{
        INFO,ERROR
    }
    public static void print(Level level,String string){
        System.out.printf("[ %s ] %s%n",level.name(),string);
    }
    public static void info(String string){
        print(Level.INFO,string);
    }
    public static void info(String string,Object... args){
        info(String.format(string,args));
    }
    public static void error(String string,Object... args){
        print(Level.ERROR,String.format(string,args));
    }
}
