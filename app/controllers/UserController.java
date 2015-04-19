package controllers;

import models.User;
//import play.data.Form;
import patches.GroupedForm;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.errors.error404;
import views.html.users.edit;
import views.html.users.signin;
import views.html.users.signup;
import views.html.users.index;

import java.util.List;

//import static play.data.Form.form;
import static patches.GroupedForm.form;

/**
 * Created by shbekti on 4/13/15.
 */
public class UserController extends Controller {

    /**
     * Sign in page.
     */
    public static Result signIn() {
        return ok(
                signin.render(form(User.class))
        );
    }

    /**
     * Handle login form submission.
     */
    public static Result authenticate() {
        GroupedForm<User> form = form(User.class, User.SignIn.class).bindFromRequest();

        if (form.hasErrors()) {
            return badRequest(signin.render(form));
        }

        session().clear();
        session("email", form.get().email);

        return redirect(
                routes.Application.index()
        );
    }

    /**
     * Logout and clean the session.
     */
    public static Result signOut() {
        session().clear();
        flash("success", "You've been signed out.");

        return redirect(
                routes.UserController.signIn()
        );
    }

    /**
     * Index page.
     */
    @Security.Authenticated(Secured.class)
    public static Result index() {
        List<User> users = User.find.all();

        return ok(
                index.render(users)
        );
    }

    /**
     * Create page.
     */
    public static Result create() {
        return ok(
                signup.render(form(User.class))
        );
    }

    /**
     * Handle create form submission.
     */
    public static Result save() {
        GroupedForm<User> form = form(User.class, User.SignUp.class).bindFromRequest();

        if (form.hasErrors()) {
            return badRequest(signup.render(form));
        }

        User user = form.get();
        user.save();

        flash("success", "A new user has been created.");

        return redirect(
                routes.UserController.signIn()
        );
    }

    /**
     * Edit page.
     */
    @Security.Authenticated(Secured.class)
    public static Result show(Long id) {
        User user = User.find.byId(id);

        if (user == null) {
            return notFound(
                error404.render()
            );
        }

        GroupedForm<User> form = form(User.class).fill(user);

        return ok(
                edit.render(id, form)
        );
    }

    /**
     * Handle update form submission.
     */
    @Security.Authenticated(Secured.class)
    public static Result update(Long id) {
        GroupedForm<User> form = form(User.class, User.Update.class).bindFromRequest();

        if (form.hasErrors()) {
            return badRequest(edit.render(id, form));
        }

        form.get().update(id);

        flash("success", "The user has been updated.");

        return redirect(
                routes.UserController.index()
        );
    }

    /**
     * Delete page.
     */
    @Security.Authenticated(Secured.class)
    public static Result delete(Long id) {
        User user = User.find.ref(id);

        if (user == null) {
            return badRequest();
        }

        user.delete();

        flash("success", "The user has been deleted.");

        return redirect(
                routes.UserController.index()
        );
    }
}
