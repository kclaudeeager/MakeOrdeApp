package com.demo.makeorders;

public class Email {
    String toEmail,body,subject;

    public String getToEmail() {
        return toEmail;
    }

    public Email(String toEmail, String body, String subject) {
        super();
        this.toEmail = toEmail;
        this.body = body;
        this.subject = subject;
    }

    public void setToEmail(String toEmail) {
        this.toEmail = toEmail;
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "Email [toEmail=" + toEmail + ", body=" + body + ", subject=" + subject + "]";
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }


}
