package controllers;

import models.Account;
import play.data.*;
import play.mvc.*;
import views.html.*;

import static play.data.Form.*;

public class Application extends Controller {

    @Security.Authenticated(Secured.class)
    public static Result index() {
        return ok(index.render("Your new application is ready."));
    }



}
