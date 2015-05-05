package controllers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Service;
import models.User;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.joda.time.DateTime;
import patches.GroupedForm;
import play.libs.F;
import play.libs.Json;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.errors.error404;
import views.html.services.edit;
import views.html.services.index;
import views.html.services.create;
import views.html.services.view;

import static patches.GroupedForm.form;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by shbekti on 4/13/15.
 */
public class ServiceWebController extends Controller {

    /**
     * Index page.
     */
    @Security.Authenticated(Secured.class)
    public static Result index() {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet("http://localhost:9000/ws/services");
        HttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
            String responseAsString = EntityUtils.toString(response.getEntity());
            ArrayNode arrayNode = (ArrayNode) Json.parse(responseAsString);
            List<Service> services = new ArrayList<>();
            Iterator<JsonNode> it = arrayNode.iterator();
            while (it.hasNext()) {
                JsonNode node = it.next();
                Service s = new Service();
                s.id = node.get("id").asLong();
                s.name = node.get("name").asText();
                s.description = node.get("description").asText();
                s.type = node.get("type").asText();
                s.license = node.get("license").asText();
                s.version = node.get("version").asText();
                s.credits = node.get("credits").asText();
                s.attributes = node.get("attributes").asText();
                s.tags = node.get("tags").asText();
                s.views = node.get("views").asText();
                s.url = node.get("url").asText();
                s.user = new User();
                s.user.email = node.get("useremail").asText();
                s.createAt = new DateTime(node.get("createat").asText());
                s.modifiedAt = new DateTime(node.get("modifiedat").asText());
                services.add(s);
            }
            return ok(
                    index.render(services)
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        return notFound();
    }

    /**
     * Create page.
     */
    @Security.Authenticated(Secured.class)
    public static Result create() {
        return ok(
                create.render(form(Service.class))
        );
    }

    /**
     * Handle create form submission.
     */
    @Security.Authenticated(Secured.class)
    public static Result save() {
        GroupedForm<Service> form = form(Service.class, Service.Create.class).bindFromRequest();

        if (form.hasErrors()) {
            return badRequest(create.render(form));
        }
        Service s = form.get();

        ObjectNode o = Json.newObject();
        s.user = User.find.where().eq("email", request().username()).findUnique();
        setDefaultValues(s);
        o.put("useremail", s.user.email);
        o.put("type", s.type);
        o.put("name", s.name);
        o.put("description", s.description);
        o.put("license", s.license);
        o.put("version", s.version);
        o.put("credits", s.credits);
        o.put("attributes", s.attributes);
        o.put("tags", s.tags);
        o.put("views", s.views);
        o.put("url", s.url);
        String content = o.toString();

        try {

            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost httpPost = new HttpPost("http://localhost:9000/ws/services/create");
            httpPost.setEntity(new StringEntity(content,
                    ContentType.create("application/json")));

            HttpResponse response = httpClient.execute(httpPost);

            flash("success", "A new service has been created.");
            return redirect(
                    routes.ServiceWebController.index()
            );

        } catch (IOException e) {
            e.printStackTrace();
        }
        return notFound();
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
    @Security.Authenticated(Secured.class)
    public static Result show(Long id) {

        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet("http://localhost:9000/ws/services/" + id);
        HttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
            String responseAsString = EntityUtils.toString(response.getEntity());
            JsonNode node = Json.parse(responseAsString);
            Service s = new Service();
            s.id = node.get("id").asLong();
            s.name = node.get("name").asText();
            s.description = node.get("description").asText();
            s.type = node.get("type").asText();
            s.license = node.get("license").asText();
            s.version = node.get("version").asText();
            s.credits = node.get("credits").asText();
            s.attributes = node.get("attributes").asText();
            s.tags = node.get("tags").asText();
            s.views = node.get("views").asText();
            s.url = node.get("url").asText();
            s.user = new User();
            s.user.email = node.get("useremail").asText();
            s.createAt = new DateTime(node.get("createat").asText());
            s.modifiedAt = new DateTime(node.get("modifiedat").asText());
            GroupedForm<Service> form = form(Service.class).fill(s);
            return ok(
                    edit.render(id, form)
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        return notFound(error404.render());
    }


    /**
     * Social view
     */
    @Security.Authenticated(Secured.class)
    public static Result view(Long id) {

        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet("http://localhost:9000/ws/services/" + id);
        HttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
            String responseAsString = EntityUtils.toString(response.getEntity());
            JsonNode node = Json.parse(responseAsString);
            Service s = new Service();
            s.id = node.get("id").asLong();
            s.name = node.get("name").asText();
            s.description = node.get("description").asText();
            s.type = node.get("type").asText();
            s.license = node.get("license").asText();
            s.version = node.get("version").asText();
            s.credits = node.get("credits").asText();
            s.attributes = node.get("attributes").asText();
            s.tags = node.get("tags").asText();
            s.views = node.get("views").asText();
            s.url = node.get("url").asText();
            s.user = new User();
            s.user.email = node.get("useremail").asText();
            s.createAt = new DateTime(node.get("createat").asText());
            s.modifiedAt = new DateTime(node.get("modifiedat").asText());
            return ok(
                    view.render(id, s)
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        return notFound(error404.render());
    }


    /**
     * Handle update form submission.
     */
    @Security.Authenticated(Secured.class)
    public static Result update(Long id) {
        GroupedForm<Service> form = form(Service.class, Service.Update.class).bindFromRequest();

        if (form.hasErrors()) {
            return badRequest(edit.render(id, form));
        }

        Service s = form.get();
        ObjectNode o = Json.newObject();
        o.put("name", s.name);
        o.put("description", s.description);
        o.put("license", s.license);
        o.put("version", s.version);
        o.put("credits", s.credits);
        o.put("attributes", s.attributes);
        o.put("tags", s.tags);
        o.put("views", s.views);
        o.put("url", s.url);
        String content = o.toString();

        try {

            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost httpPost = new HttpPost("http://localhost:9000/ws/services/"+ id + "/update");
            httpPost.setEntity(new StringEntity(content,
                    ContentType.create("application/json")));

            HttpResponse response = httpClient.execute(httpPost);

            flash("success", "The service has been updated.");

            return redirect(
                    routes.ServiceWebController.index()
            );

        } catch (IOException e) {
            e.printStackTrace();
        }

        return notFound();



    }

    /**
     * Delete page.
     */
    @Security.Authenticated(Secured.class)
    public static Result delete(Long id) {

        try {

            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost httpPost = new HttpPost("http://localhost:9000/ws/services/"+ id + "/delete");

            HttpResponse response = httpClient.execute(httpPost);

            flash("success", "The service has been deleted.");

            return redirect(
                    routes.ServiceWebController.index()
            );

        } catch (IOException e) {
            e.printStackTrace();
        }

        return notFound();
    }

}
