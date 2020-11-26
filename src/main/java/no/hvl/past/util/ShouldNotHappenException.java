package no.hvl.past.util;

public class ShouldNotHappenException extends RuntimeException {

    public ShouldNotHappenException(Class<?> clazz, String method, Throwable cause) {
        super(createMessage(clazz, method, cause.getMessage()), cause);
    }

    private static String createMessage(Class<?> clazz, String method, String message) {
        return "There was unexpected exception in class" + clazz.getCanonicalName() + "::" + method +
                " underlying cause:" + message;
    }
}
