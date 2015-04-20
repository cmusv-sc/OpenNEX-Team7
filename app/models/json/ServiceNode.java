package models.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by shbekti on 4/20/15.
 */
public class ServiceNode {

    @JsonProperty("name")
    public String name;

    @JsonProperty("id")
    public String id;

    @JsonCreator
    public ServiceNode() {

    }

}