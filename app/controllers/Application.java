package controllers;

import models.Service;
import models.User;
import play.mvc.*;
import views.html.*;

public class Application extends Controller {

    @Security.Authenticated(Secured.class)
    public static Result index() {
        /*
        User user = User.find.byId(1L);
        Service service = Service.find.byId(1L);

        service.users.add(user);
        service.save();

        service.name = "What";
        service.update();

        System.out.println(user.subscriptions);
        System.out.println(service.users);
        */

        return ok(index.render());
    }

}
