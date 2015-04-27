package models.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import models.Service;
import org.apache.commons.lang3.builder.EqualsBuilder;

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

    @Override
    public int hashCode() {
        return Integer.parseInt(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ServiceNode))
            return false;

        ServiceNode other = (ServiceNode) obj;
        return (this.id.equals(other.id));
    }

}