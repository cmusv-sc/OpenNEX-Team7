package controllers;

import models.Service;
import models.User;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.services.create;
import views.html.services.index;

import java.util.List;

import static play.data.Form.form;

/**
 * Created by shbekti on 4/13/15.
 */
public class ServiceController extends Controller {

    /**
     * Index page.
     */
    @Security.Authenticated(Secured.class)
    public static Result index() {
        List<Service> services = Service.find.all();

        return ok(
                index.render(services)
        );
    }

    /**
     * Create page.
     */
    @Security.Authenticated(Secured.class)
    public static Result create() {
        return ok(
                create.render(form(Service.class))
        );
    }

    /**
     * Store workflow.
     */
    @Security.Authenticated(Secured.class)
    public static Result store() {
        Form<Service> serviceForm = form(Service.class).bindFromRequest();

        if (serviceForm.hasErrors()) {
            flash("error", serviceForm.errors().toString());
            return badRequest(create.render(serviceForm));
        }

        Service service = serviceForm.get();
        service.user = User.find.where().eq("email", request().username()).findUnique();
        service.save();

        flash("success", "A new service has been created.");
        return redirect(
                routes.ServiceController.index()
        );
    }

}
