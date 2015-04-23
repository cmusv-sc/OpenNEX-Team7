package models;

import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * Created by shbekti on 4/12/15.
 */

@Entity
public class Service extends Model {

    @Id
    @Constraints.Min(10)
    public Long id;

    @Constraints.Required(groups = { Create.class, Update.class })
    public String name;

    public String description;
    public String type;
    public String license;
    public String version;
    public String credits;
    public String attributes;
    public String tags;
    //optional
    public String views;

    @Constraints.Required(groups = { Create.class, Update.class })
    public String url;

    @ManyToOne()
    public User user;

    // add hashset to keep track the users of the service

    public static Finder<Long, Service> find = new Finder<Long, Service>(
            Long.class, Service.class
    );

    public interface Create { }

    public interface Update { }

}