package ${package}.dagger;

import ${package}.dagger.annotations.ForApplication;

public class AppModule {
    private final Application application;
    private final Injector injector;

    public AppModule(Application application) {
        this.application = application;
        this.injector = new Injector(application);
    }

    /**
     * Allow the application context to be injected but require that it be annotated with {@link
     * ForApplication @Annotation} to explicitly differentiate it from an activity context.
     */
    @Provides
    @Singleton
    @ForApplication
    Context provideApplicationContext() {
        return application;
    }

    @Provides
    @Singleton
    Injector providesInjector() {
        return injector;
    }

    @Provides
    @Singleton
    OkHttpClient providesOkHttpClient() {
        OkHttpClient httpClient = new OkHttpClient();
        URL.setURLStreamHandlerFactory(httpClient);
        return httpClient;
    }

    @Provides
    @Singleton
    IDatastore providesDatastore() {
        return new Datastore(application);
    }

}

