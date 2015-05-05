package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Service;
import models.User;

import org.joda.time.DateTime;
import patches.GroupedForm;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.errors.error404;
import views.html.services.edit;
import views.html.services.index;
import views.html.services.create;
import views.html.services.view;

import static patches.GroupedForm.form;

import java.util.List;
import java.util.Set;

/**
 * Created by shbekti on 4/13/15.
 */
public class ServiceController extends Controller {

    /**
     * Index page.
     */
    public static Result index() {
        List<Service> services = Service.find.all();
        ArrayNode result = new ArrayNode(JsonNodeFactory.instance);
        for (Service s : services) {
            ObjectNode o = Json.newObject();
            o.put("id", s.id);
            o.put("name", s.name);
            o.put("description", s.description);
            o.put("type", s.type);
            o.put("license", s.license);
            o.put("version", s.version);
            o.put("credits", s.credits);
            o.put("attributes", s.attributes);
            o.put("tags", s.tags);
            o.put("views", s.views);
            o.put("url", s.url);
            o.put("useremail", s.user.email);
            o.put("createat", s.createAt.toString());
            o.put("modifiedat", s.modifiedAt.toString());
            result.add(o);
        }
        return ok(result);
    }

    /**
     * Handle create form submission.
     */
    public static Result save() {
        JsonNode node = request().body().asJson();
        Service service = new Service();
        service.user = User.find.where().eq("email", node.get("useremail").asText()).findUnique();
        service.name = node.get("name").asText();
        service.description = node.get("description").asText();
        service.license = node.get("license").asText();
        service.version = node.get("version").asText();
        service.credits = node.get("credits").asText();
        service.attributes = node.get("attributes").asText();
        service.tags = node.get("tags").asText();
        service.views = node.get("views").asText();
        service.url = node.get("url").asText();
        service.type = node.get("type").asText();
        service.createAt = DateTime.now();
        service.modifiedAt = service.createAt;

        service.save();
        return ok();
    }

    private static void setDefaultValues(Service service) {
        service.credits=service.user.email;
        service.license = "Creative Commons";
        service.tags = "New";
        service.version = "1.0";
        service.views = "1";
        service.type="service";
    }

    /**
     * Edit page.
     */
    public static Result show(Long id) {
        Service s = Service.find.byId(id);

        if (s == null) {
            return notFound();
        }
        ObjectNode o = Json.newObject();
        o.put("id", s.id);
        o.put("name", s.name);
        o.put("description", s.description);
        o.put("type", s.type);
        o.put("license", s.license);
        o.put("version", s.version);
        o.put("credits", s.credits);
        o.put("attributes", s.attributes);
        o.put("tags", s.tags);
        o.put("views", s.views);
        o.put("url", s.url);
        o.put("useremail", s.user.email);
        o.put("createat", s.createAt.toString());
        o.put("modifiedat", s.modifiedAt.toString());
        return ok(o);
    }


    /**
     * Handle update form submission.
     */
    public static Result update(Long id) {

        Service originalService = Service.find.byId(id);
        Set<User> users = originalService.users;


        JsonNode node = request().body().asJson();
        Service service = new Service();
        service.name = node.get("name").asText();
        service.description = node.get("description").asText();
        service.license = node.get("license").asText();
        service.version = node.get("version").asText();
        service.credits = node.get("credits").asText();
        service.attributes = node.get("attributes").asText();
        service.tags = node.get("tags").asText();
        service.views = node.get("views").asText();
        service.url = node.get("url").asText();

        if (users.size() > 0) {
            service.users = users;
        }
        service.modifiedAt = DateTime.now();
        service.update(id);
        service.notifyUsers("Service got updated.");

        return ok();
    }

    /**
     * Delete page.
     */
    public static Result delete(Long id) {
        Service service = Service.find.ref(id);

        if (service == null) {
            return badRequest();
        }

        service.notifyUsers("Service got deleted.");
        service.users.clear();
        service.save();
        service.delete();

        return ok();
    }

}
