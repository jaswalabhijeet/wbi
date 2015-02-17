import play.Application;
import play.GlobalSettings;
import play.api.mvc.EssentialFilter;
import play.filters.gzip.GzipFilter;

import data.tasks.CountryPopulateTask;
import data.tasks.IndicatorPopulateTask;
import data.tasks.Utils;

import controllers.ServerConf;

public class Global extends GlobalSettings {
    @Override
    public void onStart(Application app) {
        super.onStart(app);

        ServerConf.configureRPC();

        Utils.runTask((Runnable) new CountryPopulateTask());
        Utils.runTask((Runnable) new IndicatorPopulateTask());
    }

    @Override
    public void onStop(Application app) {
        super.onStop(app);
    }

    @SuppressWarnings("unchecked")
    public <T extends EssentialFilter> Class<T>[] filters() {
        return new Class[] {
            GzipFilter.class
        };
    }
}
