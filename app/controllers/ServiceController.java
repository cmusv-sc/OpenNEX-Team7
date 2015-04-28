package controllers;

import models.Service;
import models.User;

import patches.GroupedForm;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.errors.error404;
import views.html.services.edit;
import views.html.services.index;
import views.html.services.create;
import views.html.services.view;

import static patches.GroupedForm.form;

import java.util.List;
import java.util.Set;

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
     * Handle create form submission.
     */
    @Security.Authenticated(Secured.class)
    public static Result save() {
        GroupedForm<Service> form = form(Service.class, Service.Create.class).bindFromRequest();

        if (form.hasErrors()) {
            return badRequest(create.render(form));
        }
        Service service = form.get();
        service.user = User.find.where().eq("email", request().username()).findUnique();
        setDefaultValues(service);

        service.save();

        flash("success", "A new service has been created.");
        return redirect(
                routes.ServiceController.index()
        );
    }

    private static void setDefaultValues(Service service) {
        service.credits=service.user.email;
        service.license = "Creative Commons";
        service.tags = "New";
        service.version = "1.0";
        service.views = "1";
        service.type="service";
    }

    /**
     * Edit page.
     */
    @Security.Authenticated(Secured.class)
    public static Result show(Long id) {
        Service service = Service.find.byId(id);

        if (service == null) {
            return notFound(
                    error404.render()
            );
        }

        GroupedForm<Service> form = form(Service.class).fill(service);

        return ok(
                edit.render(id, form)
        );
    }
    /**
     * Social view
     */
    @Security.Authenticated(Secured.class)
    public static Result view(Long id) {
        Service service = Service.find.byId(id);

        if (service == null) {
            return notFound(
                    error404.render()
            );
        }

        //GroupedForm<Service> form = form(Service.class).fill(service);

        return ok(
                view.render(id, service)
        );
    }


    /**
     * Handle update form submission.
     */
    @Security.Authenticated(Secured.class)
    public static Result update(Long id) {
        GroupedForm<Service> form = form(Service.class, Service.Update.class).bindFromRequest();

        if (form.hasErrors()) {
            return badRequest(edit.render(id, form));
        }

        Service originalService = Service.find.byId(id);
        Set<User> users = originalService.users;

        Service service = form.get();
        service.users = users;
        service.update(id);
        service.notifyUsers("Service got updated.");

        flash("success", "The service has been updated.");

        return redirect(
                routes.ServiceController.index()
        );
    }

    /**
     * Delete page.
     */
    @Security.Authenticated(Secured.class)
    public static Result delete(Long id) {
        Service service = Service.find.ref(id);

        if (service == null) {
            return badRequest();
        }

        service.notifyUsers("Service got deleted.");
        service.users.clear();
        service.save();
        service.delete();

        flash("success", "The service has been deleted.");

        return redirect(
                routes.ServiceController.index()
        );
    }

}
