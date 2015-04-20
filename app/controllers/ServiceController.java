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

import static patches.GroupedForm.form;

import java.util.List;

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
        service.save();

        flash("success", "A new service has been created.");
        return redirect(
                routes.ServiceController.index()
        );
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
     * Handle update form submission.
     */
    @Security.Authenticated(Secured.class)
    public static Result update(Long id) {
        GroupedForm<Service> form = form(Service.class, Service.Update.class).bindFromRequest();

        if (form.hasErrors()) {
            return badRequest(edit.render(id, form));
        }

        form.get().update(id);

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

        service.delete();

        flash("success", "The service has been deleted.");

        return redirect(
                routes.ServiceController.index()
        );
    }

}
