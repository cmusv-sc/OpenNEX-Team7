package models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.joda.time.DateTime;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @ManyToMany(mappedBy = "subscriptions")
    public Set<User> users = new HashSet<User>();

    public DateTime createAt;
    public DateTime modifiedAt;

    // add hashset to keep track the users of the service

    public static Finder<Long, Service> find = new Finder<Long, Service>(
            Long.class, Service.class
    );

    public void notifyUsers(String message) {
        for (User user : users) {
            Notification notification = new Notification();
            notification.sender = this.name;
            notification.message = message;
            notification.timestamp = DateTime.now();
            user.notifications.add(notification);
            user.save();
        }
    }

    public interface Create { }

    public interface Update { }

}