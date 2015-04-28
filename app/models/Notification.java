package models;

import org.joda.time.DateTime;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * Created by shbekti on 4/12/15.
 */

@Entity
public class Notification extends Model {

    @Id
    @Constraints.Min(10)
    public Long id;

    @ManyToOne()
    public User user;

    public String sender;

    public String message;

    public DateTime timestamp;

    public static Finder<Long, Notification> find = new Finder<Long, Notification>(
            Long.class, Notification.class
    );

}