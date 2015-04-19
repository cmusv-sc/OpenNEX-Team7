import play.*;

/**
 * Created by shbekti on 4/18/15.
 */

public class Global extends GlobalSettings {

    public void onStart(Application app) {
        Logger.info("Application has started.");
    }

    public void onStop(Application app) {
        Logger.info("Application shutdown...");
    }

}