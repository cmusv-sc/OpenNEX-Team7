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
public class ExecutionResult extends Model {

    @Id
    @Constraints.Min(10)
    public Long id;

    @ManyToOne()
    public Workflow workflow;

    @Column(columnDefinition = "TEXT")
    public String input;

    @Column(columnDefinition = "TEXT")
    public String output;

    public DateTime timestamp;

    @ManyToOne()
    public User executor;

    public static Finder<Long, ExecutionResult> find = new Finder<Long, ExecutionResult>(
            Long.class, ExecutionResult.class
    );

}