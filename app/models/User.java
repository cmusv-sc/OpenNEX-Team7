package models;

import play.data.validation.Constraints;
import play.data.validation.ValidationError;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by shbekti on 4/12/15.
 */

@Entity
public class User extends Model {

    @Id
    @Constraints.Min(10)
    public Long id;

    @Constraints.Email
    @Column(unique = true)
    @Constraints.Required(groups = { SignIn.class, SignUp.class, Update.class })
    public String email;

    @Constraints.Required(groups = { SignIn.class, SignUp.class, Update.class })
    public String password;

    @OneToMany(cascade = CascadeType.ALL)
    public List<Workflow> workflows;

    @OneToMany(cascade = CascadeType.ALL)
    public List<Service> services;

    @ManyToMany
    public Set<Service> subscriptions;

    // add the list of messages/notifications

    public static Finder<Long, User> find = new Finder<Long, User>(
            Long.class, User.class
    );

    public static User byEmail(String email) {
        return find.where()
                .eq("email", email)
                .findUnique();
    }

    public static User byEmailAndPassword(String email, String password) {
        return find.where()
                .eq("email", email)
                .eq("password", password)
                .findUnique();
    }

    public List<ValidationError> validate(Class group) {
        List<ValidationError> errors = new ArrayList<ValidationError>();

        if (group == SignIn.class) {
            User user = byEmailAndPassword(email, password);

            if (user == null) {
                errors.add(new ValidationError("", "Invalid email or password."));
            }
        } else if (group == SignUp.class) {
            if (User.byEmail(email) != null) {
                errors.add(new ValidationError("email", "This email is already registered."));
            }
        }

        return errors.isEmpty() ? null : errors;
    }

    public interface SignIn { }

    public interface SignUp { }

    public interface Update { }

}