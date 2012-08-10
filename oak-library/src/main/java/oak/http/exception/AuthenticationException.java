package oak.http.exception;

/**
 * User: mlake
 * Date: 8/9/12
 * Time: 1:38 PM
 */
public class AuthenticationException extends OakHttpException {

    public AuthenticationException() {
        super(401, "Authentication Error");
    }
}
