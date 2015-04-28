package models;

import org.joda.time.DateTime;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * Created by shbekti on 4/12/15.
 */

@Entity
public class ExecutionResult extends Model {

    @Id
    @Constraints.Min(10)
    public Long id;

    @ManyToOne()
    public Workflow workflow;

    public String input;

    public String output;

    public DateTime timestamp;

    public static Finder<Long, ExecutionResult> find = new Finder<Long, ExecutionResult>(
            Long.class, ExecutionResult.class
    );

}