package com.pgs.spark.bigdata.web.rest.dto;

import com.pgs.spark.bigdata.domain.User;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.ZonedDateTime;
import java.util.Set;

/**
 * A DTO extending the UserDTO, which is meant to be used in the user management UI.
 */
public class ManagedUserDTO extends UserDTO {

    public static final int PASSWORD_MIN_LENGTH = 4;
    public static final int PASSWORD_MAX_LENGTH = 100;

    private Long id;

    private ZonedDateTime createdDate;

    private String lastModifiedBy;

    private ZonedDateTime lastModifiedDate;

    @NotNull
    @Size(min = PASSWORD_MIN_LENGTH, max = PASSWORD_MAX_LENGTH)
    private String password;

    public ManagedUserDTO() {
    }

    public ManagedUserDTO(User user) {
        super(user);
        this.id = user.getId();
        this.createdDate = user.getCreatedDate();
        this.lastModifiedBy = user.getLastModifiedBy();
        this.lastModifiedDate = user.getLastModifiedDate();
        this.password = null;
    }

    public ManagedUserDTO(Long id, String login, String password, String firstName, String lastName,
                          String email, boolean activated, String langKey, Set<String> authorities, ZonedDateTime createdDate, String lastModifiedBy, ZonedDateTime lastModifiedDate) {
        super(login, firstName, lastName, email, activated, langKey, authorities);
        this.id = id;
        this.createdDate = createdDate;
        this.lastModifiedBy = lastModifiedBy;
        this.lastModifiedDate = lastModifiedDate;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(ZonedDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public ZonedDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(ZonedDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "ManagedUserDTO{" +
            "id=" + id +
            ", createdDate=" + createdDate +
            ", lastModifiedBy='" + lastModifiedBy + '\'' +
            ", lastModifiedDate=" + lastModifiedDate +
            "} " + super.toString();
    }

    public static Builder builder(){
        return new Builder();
    }

    public static class Builder extends UserDTO.Builder {
        private Long id;
        private ZonedDateTime createdDate;
        private String lastModifiedBy;
        private ZonedDateTime lastModifiedDate;
        private String password;

        public Builder id(final Long id){
            this.id = id;
            return this;
        }

        public Builder createdDate(final ZonedDateTime createdDate){
            this.createdDate = createdDate;
            return this;
        }

        public Builder lastModifiedBy(final String lastModifiedBy){
            this.lastModifiedBy = lastModifiedBy;
            return this;
        }

        public Builder lastModifiedDate(final ZonedDateTime lastModifiedDate){
            this.lastModifiedDate = lastModifiedDate;
            return this;
        }

        public Builder password(final String password){
            this.password = password;
            return this;
        }

        public Builder login(final String login){
            super.login(login);
            return this;
        }

        public Builder firstName(final String firstName){
            super.firstName(firstName);
            return this;
        }

        public Builder lastName(final String lastName){
            super.lastName(lastName);
            return this;
        }

        public Builder email(final String email){
            super.email(email);
            return this;
        }

        public Builder activated(final boolean activated){
            super.activated(activated);
            return this;
        }

        public Builder langKey(final String langKey){
            super.langKey(langKey);
            return this;
        }

        public Builder authorities(final Set<String> authorities){
            super.authorities(authorities);
            return this;
        }

        public ManagedUserDTO build(){
            return new ManagedUserDTO(
                id,
                login,
                password,
                firstName,
                lastName,
                email,
                activated,
                langKey,
                authorities,
                createdDate,
                lastModifiedBy,
                lastModifiedDate
            );
        }
    }
}
