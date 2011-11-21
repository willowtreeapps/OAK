package oak.demo;

import android.app.Application;

/**
 * User: mlake Date: 9/20/11 Time: 10:53 AM
 *
 * This is just a placeholder class that lets us demonstrate the testing
 * framework
 */
public class OakApplication extends Application {

    private String message;

    @Override
    public void onCreate() {
        super.onCreate();

        message = "testMessage";

    }

    public String getMessage() {
        return message;
    }
}
