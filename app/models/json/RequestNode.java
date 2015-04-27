package models.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

/**
 * Created by shbekti on 4/20/15.
 */
public class RequestNode {

    @JsonProperty("content")
    public String content;

    @JsonCreator
    public RequestNode() {

    }
}
