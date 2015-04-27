package controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import models.Service;
import models.User;

import models.json.ServiceNode;
import models.json.WorkflowNode;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.joda.time.DateTime;
import patches.GroupedForm;
import play.libs.F;
import play.libs.ws.WS;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import play.mvc.WebSocket;
import views.html.errors.error404;
import views.html.workflows.edit;
import views.html.workflows.index;
import views.html.workflows.create;
import views.html.workflows.execute;

import static patches.GroupedForm.form;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by shbekti on 4/13/15.
 */
public class WorkflowController extends Controller {

    /**
     * Index page.
     */
    @Security.Authenticated(Secured.class)
    public static Result index() {
        List<models.Workflow> workflows = models.Workflow.find.all();

        return ok(
                index.render(workflows)
        );
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

        models.Workflow workflow = form.get();
        workflow.user = User.find.where().eq("email", request().username()).findUnique();
        workflow.save();

        flash("success", "A new workflow has been created.");
        return redirect(
                routes.WorkflowController.index()
        );
    }

    /**
     * Edit page.
     */
    @Security.Authenticated(Secured.class)
    public static Result show(Long id) {
        models.Workflow workflow = models.Workflow.find.byId(id);

        if (workflow == null) {
            return notFound(
                    error404.render()
            );
        }

        List<Service> services = Service.find.all();
        GroupedForm<models.Workflow> form = form(models.Workflow.class).fill(workflow);

        return ok(
                edit.render(id, form, services)
        );
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

        form.get().update(id);

        flash("success", "The workflow has been updated.");

        return redirect(
                routes.WorkflowController.index()
        );
    }

    /**
     * Delete page.
     */
    @Security.Authenticated(Secured.class)
    public static Result delete(Long id) {
        models.Workflow workflow = models.Workflow.find.ref(id);

        if (workflow == null) {
            return badRequest();
        }

        workflow.delete();

        flash("success", "The workflow has been deleted.");

        return redirect(
                routes.WorkflowController.index()
        );
    }

    /**
     * Execute page.
     */
    @Security.Authenticated(Secured.class)
    public static Result execute(Long id) {
        models.Workflow workflow = models.Workflow.find.byId(id);

        if (workflow == null) {
            return badRequest();
        }

        return ok(
                execute.render(workflow)
        );
    }

    /**
     * WebSocket handler.
     */
    @Security.Authenticated(Secured.class)
    public static WebSocket<String> socket(final Long id) {
        return new WebSocket<String>() {

            // Called when the Websocket Handshake is done.
            public void onReady(WebSocket.In<String> in, WebSocket.Out<String> out) {

                // For each event received on the socket,
                in.onMessage(new F.Callback<String>() {
                    public void invoke(String event) {
                        // Log events to the console
                        System.out.println(event);
                    }
                });

                // When the socket is closed.
                in.onClose(new F.Callback0() {
                    public void invoke() {
                        System.out.println("Disconnected");
                    }
                });

                // Send a single 'Hello!' message
                DateTime dt = new DateTime();
                out.write("Server time is now " + dt.toString());

                models.Workflow workflow = models.Workflow.find.byId(id);

                if (workflow == null) {
                    out.write("Workflow not found. Closing connection.");
                    out.close();
                }

                out.write("Executing workflow: " + workflow.name);

                String jsonString = workflow.content;
                out.write(jsonString);

                ObjectMapper objectMapper = new ObjectMapper();

                try {
                    WorkflowNode workflowNode = objectMapper.readValue(jsonString, WorkflowNode.class);
                    List<ServiceNode> services = workflowNode.services;

                    for (int i = 0; i < services.size(); ++i) {
                        out.write("Executing service: " + services.get(i).name);
                        Long serviceId = Long.parseLong(services.get(i).id);
                        Service service = Service.find.byId(serviceId);
                        // execute the real service

                        byte[] encodedBytes = Base64.encodeBase64("Sammy".getBytes());
                        String encodedString = new String(encodedBytes);

                        HttpClient c = new DefaultHttpClient();
                        HttpPost p = new HttpPost(service.url);

                        p.setEntity(new StringEntity("{\"content\":\"" + encodedString + "\"" + "}",
                                ContentType.create("application/json")));

                        HttpResponse r = c.execute(p);

                        BufferedReader rd = new BufferedReader(new InputStreamReader(r.getEntity().getContent()));
                        String line;

                        while ((line = rd.readLine()) != null) {
                            System.out.println(line);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                out.write("Workflow execution done.");
                out.close();
            }

        };
    }

}
