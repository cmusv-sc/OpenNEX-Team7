package models.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

/**
 * Created by shbekti on 4/20/15.
 */
public class WorkflowNode {

    @JsonProperty("services")
    public ArrayList<ServiceNode> services;

    @JsonCreator
    public WorkflowNode() {

    }
}
