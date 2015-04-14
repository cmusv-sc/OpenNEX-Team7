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
public class User extends Model {

    @Id
    @Constraints.Min(10)
    public Long id;

    @Constraints.Required
    public String email;

    @Constraints.Required
    public String password;

    @OneToMany(cascade = CascadeType.ALL)
    public List<Workflow> workflows;

    @OneToMany(cascade = CascadeType.ALL)
    public List<Software> softwares;

    public static Finder<Long, User> find = new Finder<Long, User>(
            Long.class, User.class
    );

    public static User get(String email, String password) {

        System.out.println(email + " " + password); // Check if form data is passed.

        return find.where()
                .eq("email", email)
                .eq("password", password)
                .findUnique();

    }

    public String validate() {
        if ((email == null) || (password == null)) {
            return "Email or password cannot be blank.";
        }

        return null;
    }

}