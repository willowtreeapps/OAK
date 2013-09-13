package ${package};


import java.lang.annotation.Retention;

import javax.inject.Qualifier;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by ericrichardson on 7/26/13.
 */
@Qualifier
@Retention(RUNTIME)
public @interface ForApplication {

}
