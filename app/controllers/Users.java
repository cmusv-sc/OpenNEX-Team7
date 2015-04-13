package controllers;

import models.Account;
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
                login.render(form(Account.class))
        );
    }

    /**
     * Handle login form submission.
     */
    public static Result authenticate() {
        Form<Account> loginForm = form(Account.class).bindFromRequest();

        if (loginForm.hasErrors()) {
            flash("error", loginForm.errors().toString());
            return badRequest(login.render(loginForm));
        }

        String email = loginForm.get().email;
        String password = loginForm.get().password;

        Account checkAccount = Account.get(email, password);

        if (checkAccount == null) {
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
                signup.render(form(Account.class))
        );
    }

    /**
     * Handle signup form submission.
     */
    public static Result register() {
        Form<Account> signupForm = form(Account.class).bindFromRequest();

        if (signupForm.hasErrors()) {
            flash("error", signupForm.errors().toString());
            return badRequest(signup.render(signupForm));
        }

        Account account = new Account();
        account.email = signupForm.get().email;
        account.password = signupForm.get().password;
        account.save();

        flash("success", "New account has been created.");
        return redirect(
                routes.Users.login()
        );
    }
}
