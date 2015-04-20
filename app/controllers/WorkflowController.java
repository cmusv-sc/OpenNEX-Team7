package controllers;

import models.Service;
import models.User;
import models.Workflow;

import patches.GroupedForm;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.errors.error404;
import views.html.workflows.edit;
import views.html.workflows.index;
import views.html.workflows.create;

import static patches.GroupedForm.form;

import java.util.List;

/**
 * Created by shbekti on 4/13/15.
 */
public class WorkflowController extends Controller {

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
        List<Service> services = Service.find.all();

        return ok(
                create.render(form(Workflow.class), services)
        );
    }

    /**
     * Handle create form submission.
     */
    @Security.Authenticated(Secured.class)
    public static Result save() {
        GroupedForm<Workflow> form = form(Workflow.class, Workflow.Create.class).bindFromRequest();

        if (form.hasErrors()) {
            List<Service> services = Service.find.all();
            return badRequest(create.render(form, services));
        }

        Workflow workflow = form.get();
        workflow.user = User.find.where().eq("email", request().username()).findUnique();
        workflow.save();

        flash("success", "A new workflow has been created.");
        return redirect(
                routes.WorkflowController.index()
        );
    }

    /**
     * Edit page.
     */
    @Security.Authenticated(Secured.class)
    public static Result show(Long id) {
        Workflow workflow = Workflow.find.byId(id);

        if (workflow == null) {
            return notFound(
                    error404.render()
            );
        }

        List<Service> services = Service.find.all();
        GroupedForm<Workflow> form = form(Workflow.class).fill(workflow);

        return ok(
                edit.render(id, form, services)
        );
    }

    /**
     * Handle update form submission.
     */
    @Security.Authenticated(Secured.class)
    public static Result update(Long id) {
        GroupedForm<Workflow> form = form(Workflow.class, Workflow.Update.class).bindFromRequest();

        if (form.hasErrors()) {
            List<Service> services = Service.find.all();
            return badRequest(edit.render(id, form, services));
        }

        form.get().update(id);

        flash("success", "The workflow has been updated.");

        return redirect(
                routes.WorkflowController.index()
        );
    }

    /**
     * Delete page.
     */
    @Security.Authenticated(Secured.class)
    public static Result delete(Long id) {
        Workflow workflow = Workflow.find.ref(id);

        if (workflow == null) {
            return badRequest();
        }

        workflow.delete();

        flash("success", "The workflow has been deleted.");

        return redirect(
                routes.WorkflowController.index()
        );
    }

    /**
     * Execute page.
     */
    @Security.Authenticated(Secured.class)
    public static Result execute(Long id) {
        Workflow workflow = Workflow.find.byId(id);

        if (workflow == null) {
            return badRequest();
        }

        flash("success", "The workflow has been executed.");

        return redirect(
                routes.WorkflowController.index()
        );
    }

}
