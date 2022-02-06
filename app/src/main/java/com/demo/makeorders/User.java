package com.demo.makeorders;

import java.util.Objects;

public class User {
    String company,email;
    int role;

    public User(String company, String email, int role) {
        this.company = company;
        this.email = email;
        this.role = role;
    }

    public User() {

    }

    @Override
    public String toString() {
        return "User{" +
                "company='" + company + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return role == user.role && company.equals(user.company) && email.equals(user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(company, email, role);
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public String getCompany() {
        return company;
    }

    public String getEmail() {
        return email;
    }

    public int getRole() {
        return role;
    }
}
