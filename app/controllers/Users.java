package controllers;

import models.User;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.login;
import views.html.signup;

import static play.data.Form.form;

/**
 * Created by shbekti on 4/13/15.
 */
public class Users extends Controller {

    /**
     * Login page.
     */
    public static Result login() {
        return ok(
                login.render(form(User.class))
        );
    }

    /**
     * Handle login form submission.
     */
    public static Result authenticate() {
        Form<User> loginForm = form(User.class).bindFromRequest();

        if (loginForm.hasErrors()) {
            flash("error", loginForm.errors().toString());
            return badRequest(login.render(loginForm));
        }

        String email = loginForm.get().email;
        String password = loginForm.get().password;

        User checkUser = User.get(email, password);

        if (checkUser == null) {
            flash("error", "Invalid email or password.");
            return badRequest(login.render(loginForm));
        }

        session().clear();
        session("email", email);
        return redirect(
                routes.Application.index()
        );
    }

    /**
     * Logout and clean the session.
     */
    public static Result logout() {
        session().clear();
        flash("success", "You've been logged out.");
        return redirect(
                routes.Users.login()
        );
    }

    /**
     * Signup page.
     */
    public static Result signup() {
        return ok(
                signup.render(form(User.class))
        );
    }

    /**
     * Handle signup form submission.
     */
    public static Result register() {
        Form<User> signupForm = form(User.class).bindFromRequest();

        if (signupForm.hasErrors()) {
            flash("error", signupForm.errors().toString());
            return badRequest(signup.render(signupForm));
        }

        User user = new User();
        user.email = signupForm.get().email;
        user.password = signupForm.get().password;
        user.save();

        flash("success", "New user has been created.");
        return redirect(
                routes.Users.login()
        );
    }
}
