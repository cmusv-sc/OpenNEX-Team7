package controllers;

import models.Software;
import models.User;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.softwares.create;
import views.html.softwares.index;

import java.util.List;

import static play.data.Form.form;

/**
 * Created by shbekti on 4/13/15.
 */
public class Softwares extends Controller {

    /**
     * Index page.
     */
    @Security.Authenticated(Secured.class)
    public static Result index() {
        List<Software> softwares = Software.find.all();

        return ok(
                index.render(softwares)
        );
    }

    /**
     * Create page.
     */
    @Security.Authenticated(Secured.class)
    public static Result create() {
        return ok(
                create.render(form(Software.class))
        );
    }

    /**
     * Store workflow.
     */
    @Security.Authenticated(Secured.class)
    public static Result store() {
        Form<Software> softwareForm = form(Software.class).bindFromRequest();

        if (softwareForm.hasErrors()) {
            flash("error", softwareForm.errors().toString());
            return badRequest(create.render(softwareForm));
        }

        Software software = softwareForm.get();
        software.owner = User.find.where().eq("email", request().username()).findUnique();
        software.save();

        flash("success", "New software has been created.");
        return redirect(
                routes.Softwares.index()
        );
    }

}
