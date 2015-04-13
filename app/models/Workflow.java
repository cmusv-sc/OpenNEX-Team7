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

    @Constraints.Required
    public String name;

    @Constraints.Required
    public String content;

    @ManyToOne(cascade = CascadeType.ALL)
    public Accounts owner;

    public static Finder<Long, Workflow> find = new Finder<Long, Workflow>(
            Long.class, Workflow.class
    );

}