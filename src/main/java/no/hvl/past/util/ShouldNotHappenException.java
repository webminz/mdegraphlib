package no.hvl.past.util;

public class ShouldNotHappenException extends RuntimeException {

    private Throwable underlying;

    public ShouldNotHappenException(Class<?> clazz, Throwable t) {
        super(createMessage(clazz, Thread.currentThread().getStackTrace()[1].getMethodName(), t.getMessage()), t);
    }

    public ShouldNotHappenException(Class<?> clazz, String message) {
        super(createMessage(clazz, Thread.currentThread().getStackTrace()[1].getMethodName(), message));
    }

    private static String createMessage(Class<?> clazz, String method, String message) {
        return "There was unexpected exception in class" + clazz.getCanonicalName() + "::" + method +
                " underlying cause:" + message;
    }
}
