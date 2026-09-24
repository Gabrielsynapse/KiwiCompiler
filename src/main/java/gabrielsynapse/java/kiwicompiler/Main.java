package gabrielsynapse.java.kiwicompiler;


public class Main {
    public static void init(){

    }
    static void main(String[] args) {
        for(String arg:args){
            if(arg.equals("init")){
                init();
            }
        }
    }
}
