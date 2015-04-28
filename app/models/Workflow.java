package models;

import javax.persistence.*;

import org.joda.time.DateTime;
import play.db.ebean.*;
import play.data.validation.*;

import java.util.List;

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

    @Column(columnDefinition = "TEXT")
    @Constraints.Required(groups = { Create.class, Update.class })
    public String content;

    @ManyToOne()
    public User user;

    public String version;
    public DateTime createAt;
    public DateTime modifiedAt;

    @OneToMany(cascade = CascadeType.ALL)
    public List<ExecutionResult> results;

    public static Finder<Long, Workflow> find = new Finder<Long, Workflow>(
            Long.class, Workflow.class
    );

    public interface Create { }

    public interface Update { }

}