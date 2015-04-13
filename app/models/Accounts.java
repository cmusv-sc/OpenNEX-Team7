package models;

import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;

/**
 * Created by shbekti on 4/12/15.
 */

@Entity
public class Accounts extends Model {

    @Id
    @Constraints.Min(10)
    public Long id;

    @Constraints.Required
    public String email;

    @Constraints.Required
    public String password;

    @OneToMany(cascade = CascadeType.ALL)
    public List<Workflow> workflows;

    public static Finder<Long, Accounts> find = new Finder<Long, Accounts>(
            Long.class, Accounts.class
    );

    public static Accounts authenticate(String email, String password) {

        System.out.println(email + " " + password); // Check if form data is passed.

        return find.where()
                .eq("email", email)
                .eq("password", password)
                .findUnique();

    }

}