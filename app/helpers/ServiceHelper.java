package helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import models.Service;
import models.json.ServiceNode;
import models.json.WorkflowNode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by shbekti on 4/27/15.
 */
public class ServiceHelper {

    public static List<Service> getUniqueServicesFromJson(String json) {
        ObjectMapper objectMapper = new ObjectMapper();

        List<Service> result = new ArrayList<Service>();

        try {
            WorkflowNode workflowNode = objectMapper.readValue(json, WorkflowNode.class);
            List<ServiceNode> services = workflowNode.services;
            Set<ServiceNode> serviceSet = new HashSet<ServiceNode>(services);

            for (ServiceNode serviceNode : serviceSet) {
                Long serviceId = Long.parseLong(serviceNode.id);
                Service service = Service.find.byId(serviceId);
                result.add(service);
            }
        } catch (Exception e) {

        }

        return result;
    }
}
