package xc0ffee.taxicab.models;

import com.parse.ParseClassName;
import com.parse.ParseUser;

@ParseClassName("User")
public class User extends ParseUser {

    private static final String ROLE = "role";
    private static final String NAME = "name";
    private static final String PHONE = "phone";

    public User() {
    }

    public void setRole() {
        put(ROLE, "user");
    }

    public void setName(String name) {
        put(NAME, name);
    }

    public void setPhone(String phone) {
        put(PHONE, phone);
    }

    public String getName() {
        return getString(NAME);
    }

    public String getEmail() {
        return getEmail();
    }

    public String getRole() {
        return "user";
    }

    public String getPhone() {
        return getString(PHONE);
    }
}
