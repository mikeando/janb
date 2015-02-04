package janb.mxl;

/**
 * Created by michaelanderson on 4/02/2015.
 */
public class MxlConstructionException extends Exception {
    public MxlConstructionException(String message) {
        super(message);
    }

    public MxlConstructionException(String message, Exception cause) {
        super(message,cause);
    }
}
