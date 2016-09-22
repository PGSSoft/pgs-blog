package com.pgs.spark.bigdata.web.rest.dto;

import com.pgs.spark.bigdata.domain.Authority;
import com.pgs.spark.bigdata.domain.User;
import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A DTO representing a user, with his authorities.
 */
public class UserDTO {

    @Pattern(regexp = "^[a-z0-9]*$")
    @NotNull
    @Size(min = 1, max = 50)
    private String login;

    @Size(max = 50)
    private String firstName;

    @Size(max = 50)
    private String lastName;

    @Email
    @Size(min = 5, max = 100)
    private String email;

    private boolean activated = false;

    @Size(min = 2, max = 5)
    private String langKey;

    private Set<String> authorities;

    public UserDTO() {
    }

    public UserDTO(User user) {
        this(user.getLogin(), user.getFirstName(), user.getLastName(),
            user.getEmail(), user.getActivated(), user.getLangKey(),
            user.getAuthorities().stream().map(Authority::getName)
                .collect(Collectors.toSet()));
    }

    public UserDTO(String login, String firstName, String lastName,
                   String email, boolean activated, String langKey, Set<String> authorities) {

        this.login = login;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.activated = activated;
        this.langKey = langKey;
        this.authorities = authorities;
    }

    public String getLogin() {
        return login;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public boolean isActivated() {
        return activated;
    }

    public String getLangKey() {
        return langKey;
    }

    public Set<String> getAuthorities() {
        return authorities;
    }



    @Override
    public String toString() {
        return "UserDTO{" +
            "login='" + login + '\'' +
            ", firstName='" + firstName + '\'' +
            ", lastName='" + lastName + '\'' +
            ", email='" + email + '\'' +
            ", activated=" + activated +
            ", langKey='" + langKey + '\'' +
            ", authorities=" + authorities +
            "}";
    }

    public static Builder builder(){
        return new Builder();
    }

    public static class Builder {
        protected String login;
        protected String firstName;
        protected String lastName;
        protected String email;
        protected boolean activated;
        protected String langKey;
        protected Set<String> authorities;

        public Builder login(final String login){
            this.login = login;
            return this;
        }

        public Builder firstName(final String firstName){
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(final String lastName){
            this.lastName = lastName;
            return this;
        }

        public Builder email(final String email){
            this.email = email;
            return this;
        }

        public Builder activated(final boolean activated){
            this.activated = activated;
            return this;
        }

        public Builder langKey(final String langKey){
            this.langKey = langKey;
            return this;
        }

        public Builder authorities(final Set<String> authorities){
            this.authorities = authorities;
            return this;
        }

        public UserDTO build(){
            return new UserDTO(
                login,
                firstName,
                lastName,
                email,
                activated,
                langKey,
                authorities
            );
        }
    }
}
