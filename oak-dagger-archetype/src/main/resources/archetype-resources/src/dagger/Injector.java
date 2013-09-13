package $

{package}.dagger;

        import android.content.Context;

/**
 * User: evantatarka Date: 7/30/13 Time: 4:30 PM
 */
public class Injector {
    public static void inject(Object toInject, Context context) {
        ((IObjectGraph) context.getApplicationContext()).getObjectGraph().inject(toInject);
    }

    public static void inject(Context context) {
        ((IObjectGraph) context.getApplicationContext()).getObjectGraph().inject(context);
    }

    private Context mContext;

    public Injector(Context context) {
        mContext = context;
    }

    public void inject(Object toInject) {
        ((IObjectGraph) mContext.getApplicationContext()).getObjectGraph().inject(toInject);
    }
}