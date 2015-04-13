package controllers;

import models.Workflow;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.workflows.index;

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

}
