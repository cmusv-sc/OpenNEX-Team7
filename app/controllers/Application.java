package controllers;

import models.Accounts;
import play.data.*;
import play.mvc.*;
import views.html.*;
import static play.data.Form.*;

public class Application extends Controller {

    // -- Authentication

    public static class Login {

        public String email;
        public String password;

        public String validate() {
            if (Accounts.authenticate(email, password) == null) {
                return "Invalid user or password";
            }
            return null;
        }

    }

    @Security.Authenticated(Secured.class)
    public static Result index() {
        return ok(index.render("Your new application is ready."));
    }

    /**
     * Login page.
     */
    public static Result login() {
        return ok(
                login.render(form(Login.class))
        );
    }

    /**
     * Handle login form submission.
     */
    public static Result authenticate() {
        Form<Login> loginForm = form(Login.class).bindFromRequest();
        if (loginForm.hasErrors()) {
            return badRequest(login.render(loginForm));
        } else {
            session().clear();
            session("email", loginForm.get().email);
            return redirect(
                    routes.Application.index()
            );
        }
    }

    /**
     * Logout and clean the session.
     */
    public static Result logout() {
        session().clear();
        flash("success", "You've been logged out");
        return redirect(
                routes.Application.login()
        );
    }

}
