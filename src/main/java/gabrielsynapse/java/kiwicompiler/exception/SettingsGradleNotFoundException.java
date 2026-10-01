package gabrielsynapse.java.kiwicompiler.exception;

public class SettingsGradleNotFoundException extends RuntimeException {
    public SettingsGradleNotFoundException(String message) {
        super("arquivo settings.gradle nao existe ou nao é arquivo: " + message);
    }
}
