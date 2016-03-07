package xc0ffee.taxicab.models;

import com.parse.ParseClassName;
import com.parse.ParseUser;

@ParseClassName("User")
public class User extends ParseUser {

    public static final String ROLE = "role";

    public User() {
    }

    public void setRole() {
        put(ROLE, "user");
    }
}
