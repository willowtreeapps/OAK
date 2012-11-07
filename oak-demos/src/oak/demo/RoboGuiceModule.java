package oak.demo;

import com.google.inject.AbstractModule;

/**
 * Generated from archetype
 */
public class RoboGuiceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(EncryptedSharedPreferences.class).toProvider(EncryptedPreferencesProvider.class);
    }
}
