package controllers;

import models.User;
import models.Workflow;
import static play.data.Form.form;

import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.workflows.index;
import views.html.workflows.create;

import java.util.List;

/**
 * Created by shbekti on 4/13/15.
 */
public class Workflows extends Controller {

    /**
     * Index page.
     */
    @Security.Authenticated(Secured.class)
    public static Result index() {
        List<Workflow> workflows = Workflow.find.all();

        return ok(
                index.render(workflows)
        );
    }

    /**
     * Create page.
     */
    @Security.Authenticated(Secured.class)
    public static Result create() {
        return ok(
                create.render(form(Workflow.class))
        );
    }

    /**
     * Store workflow.
     */
    @Security.Authenticated(Secured.class)
    public static Result store() {
        Form<Workflow> workflowForm = form(Workflow.class).bindFromRequest();

        if (workflowForm.hasErrors()) {
            flash("error", workflowForm.errors().toString());
            return badRequest(create.render(workflowForm));
        }

        Workflow workflow = workflowForm.get();
        workflow.owner = User.find.where().eq("email", request().username()).findUnique();
        workflow.save();

        flash("success", "New workflow has been created.");
        return redirect(
                routes.Workflows.index()
        );
    }

}
