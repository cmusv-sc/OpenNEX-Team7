package models;

import javax.persistence.*;

import play.db.ebean.*;
import play.data.validation.*;

/**
 * Created by shbekti on 4/12/15.
 */

@Entity
public class Workflow extends Model {

    @Id
    @Constraints.Min(10)
    public Long id;

    @Constraints.Required(groups = { Create.class, Update.class })
    public String name;

    public String description;

    @Constraints.Required(groups = { Create.class, Update.class })
    public String content;

    @ManyToOne(cascade = CascadeType.ALL)
    public User user;

    public static Finder<Long, Workflow> find = new Finder<Long, Workflow>(
            Long.class, Workflow.class
    );

    public interface Create { }

    public interface Update { }

}