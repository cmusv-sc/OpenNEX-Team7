package controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import helpers.ServiceHelper;
import models.ExecutionResult;
import models.Service;
import models.User;

import models.Workflow;
import models.json.RequestNode;
import models.json.ServiceNode;
import models.json.WorkflowNode;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.joda.time.DateTime;
import patches.GroupedForm;
import play.libs.F;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import play.mvc.WebSocket;
import views.html.errors.error404;
import views.html.workflows.edit;
import views.html.workflows.index;
import views.html.workflows.create;
import views.html.workflows.execute;
import views.html.workflows.view;

import static patches.GroupedForm.form;

import java.util.HashSet;
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
        subscribe(workflow);

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

        Workflow workflow = Workflow.find.byId(id);
        unsubscribe(workflow);

        form.get().update(id);

        Workflow updatedWorkflow = Workflow.find.byId(id);
        subscribe(updatedWorkflow);

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

        unsubscribe(workflow);
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

    /**
     * WebSocket handler.
     */
    @Security.Authenticated(Secured.class)
    public static WebSocket<String> socket(final Long id) {
        return new WebSocket<String>() {

            // Called when the Websocket Handshake is done.
            public void onReady(WebSocket.In<String> in, final WebSocket.Out<String> out) {

                // For each event received on the socket,
                in.onMessage(new F.Callback<String>() {
                    public void invoke(String event) {
                        // Log events to the console
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

                            byte[] encodedBytes = Base64.encodeBase64(event.getBytes());
                            String encodedString = new String(encodedBytes);

                            String immediateResult = "{\"content\":\"" + encodedString + "\"" + "}";

                            for (int i = 0; i < services.size(); ++i) {
                                out.write("Executing service: " + services.get(i).name);
                                Long serviceId = Long.parseLong(services.get(i).id);
                                Service service = Service.find.byId(serviceId);
                                // execute the real service

                                HttpClient httpClient = HttpClientBuilder.create().build();
                                HttpPost httpPost = new HttpPost(service.url);

                                httpPost.setEntity(new StringEntity(immediateResult,
                                        ContentType.create("application/json")));

                                HttpResponse response = httpClient.execute(httpPost);

                                String responseAsString = EntityUtils.toString(response.getEntity());
                                immediateResult = responseAsString;
                            }

                            RequestNode requestNode = objectMapper.readValue(immediateResult, RequestNode.class);

                            byte[] decodedBytes = Base64.decodeBase64(requestNode.content.getBytes());
                            String decodedString = new String(decodedBytes);

                            out.write("Output: " + decodedString);

                            ExecutionResult result = new ExecutionResult();
                            result.input = event;
                            result.output = decodedString;
                            result.timestamp = DateTime.now();
                            result.executor = null;
                            workflow.results.add(result);
                            workflow.save();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        out.write("Workflow execution done.");
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
            }

        };
    }

    private static void subscribe(Workflow workflow) {
        workflow.user = User.find.where().eq("email", request().username()).findUnique();
        workflow.save();

        String servicesJson = workflow.content;

        List<Service> services = ServiceHelper.getUniqueServicesFromJson(servicesJson);

        for (Service service : services) {
            service.users.add(workflow.user);
            service.save();
        }
    }

    private static void unsubscribe(Workflow workflow) {
        User user = workflow.user;
        List<Workflow> userWorkflows = user.workflows;

        HashSet<Service> otherWorkflowServices = new HashSet<>();

        for (Workflow userWorkflow : userWorkflows) {
            if (userWorkflow.id.equals(workflow.id)) continue;

            List<Service> services = ServiceHelper.getUniqueServicesFromJson(userWorkflow.content);
            otherWorkflowServices.addAll(services);
        }

        HashSet<Service> currentWorkflowServices = new HashSet<>(ServiceHelper.getUniqueServicesFromJson(workflow.content));

        currentWorkflowServices.removeAll(otherWorkflowServices);

        for (Service service : currentWorkflowServices) {
            service.users.remove(workflow.user);
            service.save();
        }
    }

}
