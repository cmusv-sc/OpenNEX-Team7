package controllers;

import models.User;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.users.signin;
import views.html.users.signup;

import static play.data.Form.form;

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
        Form<User> signInForm = form(User.class).bindFromRequest();

        if (signInForm.hasErrors()) {
            flash("error", signInForm.errors().toString());
            return badRequest(signin.render(signInForm));
        }

        String email = signInForm.get().email;
        String password = signInForm.get().password;

        User checkUser = User.get(email, password);

        if (checkUser == null) {
            flash("error", "Invalid email or password.");
            return badRequest(signin.render(signInForm));
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
    public static Result signOut() {
        session().clear();
        flash("success", "You've been signed out out.");
        return redirect(
                routes.UserController.signIn()
        );
    }

    /**
     * Sign up page.
     */
    public static Result signUp() {
        return ok(
                signup.render(form(User.class))
        );
    }

    /**
     * Handle sign up form submission.
     */
    public static Result store() {
        Form<User> signUpForm = form(User.class).bindFromRequest();

        if (signUpForm.hasErrors()) {
            flash("error", signUpForm.errors().toString());
            return badRequest(signup.render(signUpForm));
        }

        User user = new User();
        user.email = signUpForm.get().email;
        user.password = signUpForm.get().password;
        user.save();

        flash("success", "A new user has been created.");
        return redirect(
                routes.UserController.signIn()
        );
    }
}
