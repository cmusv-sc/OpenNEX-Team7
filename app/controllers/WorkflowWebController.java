package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Service;
import models.User;

import models.Workflow;
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
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.errors.error404;
import views.html.workflows.edit;
import views.html.workflows.index;
import views.html.workflows.create;
import views.html.workflows.execute;
import views.html.workflows.view;

import static patches.GroupedForm.form;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * Created by shbekti on 4/13/15.
 */
public class WorkflowWebController extends Controller {

    /**
     * Index page.
     */
    @Security.Authenticated(Secured.class)
    public static Result index() {

        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet("http://localhost:9000/ws/workflows");
        HttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
            String responseAsString = EntityUtils.toString(response.getEntity());
            ArrayNode arrayNode = (ArrayNode) Json.parse(responseAsString);
            List<Workflow> workflows = new ArrayList<>();
            Iterator<JsonNode> it = arrayNode.iterator();
            while (it.hasNext()) {
                JsonNode node = it.next();
                Workflow w = new Workflow();
                w.id = node.get("id").asLong();
                w.name = node.get("name").asText();
                w.description = node.get("description").asText();
                w.content = node.get("content").asText();
                w.version = node.get("version").asText();
                w.user = new User();
                w.user.email = node.get("useremail").asText();
                w.createAt = new DateTime(node.get("createat").asText());
                w.modifiedAt = new DateTime(node.get("modifiedat").asText());
                workflows.add(w);
            }
            return ok(
                    index.render(workflows)
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
        List<Service> services = Service.find.all();

        return ok(
                create.render(form(models.Workflow.class), services)
        );
    }

    /**
     * Handle create form submission.
     */
    @Security.Authenticated(Secured.class)
    public static Result save() {
        GroupedForm<models.Workflow> form = form(models.Workflow.class, models.Workflow.Create.class).bindFromRequest();


        if (form.hasErrors()) {
            List<Service> services = Service.find.all();
            return badRequest(create.render(form, services));
        }

        models.Workflow w = form.get();

        ObjectNode o = Json.newObject();
        w.user = User.find.where().eq("email", request().username()).findUnique();
        o.put("useremail", w.user.email);
        o.put("name", w.name);
        o.put("description", w.description);
        o.put("content", w.content);
        o.put("version", w.version);
        String content = o.toString();

        try {

            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost httpPost = new HttpPost("http://localhost:9000/ws/workflows/create");
            httpPost.setEntity(new StringEntity(content,
                    ContentType.create("application/json")));

            HttpResponse response = httpClient.execute(httpPost);

            flash("success", "A new workflow has been created.");
            return redirect(
                    routes.WorkflowWebController.index()
            );

        } catch (IOException e) {
            e.printStackTrace();
        }
        return notFound();
    }

    /**
     * Edit page.
     */
    @Security.Authenticated(Secured.class)
    public static Result show(Long id) {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpClient httpClientS = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet("http://localhost:9000/ws/workflows/" + id);
        HttpGet httpGetS = new HttpGet("http://localhost:9000/ws/services");
        HttpResponse response = null;
        HttpResponse responseS = null;
        try {
            response = httpClient.execute(httpGet);
            String responseAsString = EntityUtils.toString(response.getEntity());
            JsonNode node = Json.parse(responseAsString);
            Workflow w = new Workflow();
            w.id = node.get("id").asLong();
            w.name = node.get("name").asText();
            w.description = node.get("description").asText();
            w.version = node.get("version").asText();
            w.content = node.get("content").asText();
            w.createAt = new DateTime(node.get("createat").asText());
            w.modifiedAt = new DateTime(node.get("modifiedat").asText());
            GroupedForm<Workflow> form = form(Workflow.class).fill(w);

            responseS = httpClientS.execute(httpGetS);
            responseAsString = EntityUtils.toString(responseS.getEntity());
            ArrayNode arrayNode = (ArrayNode) Json.parse(responseAsString);
            List<Service> services = new ArrayList<>();
            Iterator<JsonNode> it = arrayNode.iterator();
            while (it.hasNext()) {
                node = it.next();
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
                    edit.render(id, form, services)
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


        GroupedForm<models.Workflow> form = form(models.Workflow.class, models.Workflow.Update.class).bindFromRequest();

        if (form.hasErrors()) {
            List<Service> services = Service.find.all();
            return badRequest(edit.render(id, form, services));
        }

        models.Workflow w = form.get();

        ObjectNode o = Json.newObject();
        w.user = User.find.where().eq("email", request().username()).findUnique();
        o.put("useremail", w.user.email);
        o.put("name", w.name);
        o.put("description", w.description);
        o.put("content", w.content);
        o.put("version", w.version);
        String content = o.toString();

        try {

            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost httpPost = new HttpPost("http://localhost:9000/ws/workflows/" + id + "/update");
            httpPost.setEntity(new StringEntity(content,
                    ContentType.create("application/json")));

            HttpResponse response = httpClient.execute(httpPost);

            flash("success", "The workflow has been updated.");
            return redirect(
                    routes.WorkflowWebController.index()
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
            HttpPost httpPost = new HttpPost("http://localhost:9000/ws/workflows/"+ id + "/delete");

            HttpResponse response = httpClient.execute(httpPost);

            flash("success", "The workflow has been deleted.");

            return redirect(
                    routes.WorkflowWebController.index()
            );

        } catch (IOException e) {
            e.printStackTrace();
        }

        return notFound();
    }

    /**
     * Execute page.
     */
    @Security.Authenticated(Secured.class)
    public static Result execute(Long id) {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet("http://localhost:9000/ws/workflows/" + id + "/execute");
        HttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
            String responseAsString = EntityUtils.toString(response.getEntity());
            JsonNode node = Json.parse(responseAsString);
            Workflow w = new Workflow();
            w.id = node.get("id").asLong();
            w.name = node.get("name").asText();
            w.description = node.get("description").asText();
            w.version = node.get("version").asText();
            w.content = node.get("content").asText();
            w.createAt = new DateTime(node.get("createat").asText());
            w.modifiedAt = new DateTime(node.get("modifiedat").asText());

            return ok(
                    execute.render(w)
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        return badRequest();
    }

    /**
     * Results view
     */
    @Security.Authenticated(Secured.class)
    public static Result view(Long id) {
        Workflow workflow = Workflow.find.byId(id);

        if (workflow == null) {
            return notFound(
                    error404.render()
            );
        }

        return ok(
                view.render(id, workflow)
        );
    }

}
