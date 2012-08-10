package oak.http.exception;

/**
 * User: mlake
 * Date: 8/9/12
 * Time: 1:37 PM
 */
public class OakHttpException extends RuntimeException {

    private int responseCode;

    public OakHttpException(int responseCode) {
        super("Connection Error");
        this.responseCode = responseCode;
    }

    public OakHttpException(int responseCode, String detailMessage) {
        super(detailMessage);
        this.responseCode = responseCode;
    }

    @Override
    public String getMessage() {
        return (super.getMessage() == null? "": super.getMessage()) + "\nResponse code: " + responseCode;
    }
}
