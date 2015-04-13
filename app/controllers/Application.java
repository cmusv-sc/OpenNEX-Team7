package controllers;

import models.Account;
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
            if (Account.authenticate(email, password) == null) {
                return "Invalid email or password.";
            }
            return null;
        }

    }

    public static class Signup {

        public String email;
        public String password;

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
        flash("success", "You've been logged out.");
        return redirect(
                routes.Application.login()
        );
    }

    /**
     * Signup page.
     */
    public static Result signup() {
        return ok(
                signup.render(form(Signup.class))
        );
    }

    /**
     * Handle signup form submission.
     */
    public static Result register() {
        Form<Signup> signupForm = form(Signup.class).bindFromRequest();
        if (signupForm.hasErrors()) {
            return badRequest(signup.render(signupForm));
        } else {
            Account account = new Account();
            account.email = signupForm.get().email;
            account.password = signupForm.get().password;
            account.save();

            flash("success", "New account has been created.");

            return redirect(
                    routes.Application.login()
            );
        }
    }

}
