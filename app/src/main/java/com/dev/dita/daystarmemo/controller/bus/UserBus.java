package com.dev.dita.daystarmemo.controller.bus;


public class UserBus {
    public static class LoginEvent {
        public String username;
        public String password;

        public LoginEvent(String username, String password) {
            this.username = username;
            this.password = password;
        }

        @Override
        public String toString() {
            return "LoginBus{" +
                    "username='" + username + '\'' +
                    ", password='" + password + '\'' +
                    '}';
        }
    }

    public static class LoginResult {
        public Boolean error;
        public String message;
    }

    public static class RegisterEvent {
        public String username;
        public String email;
        public String password;

        public RegisterEvent(String username, String email, String password) {
            this.username = username;
            this.email = email;
            this.password = password;
        }

        @Override
        public String toString() {
            return "RegisterEvent{" +
                    "username='" + username + '\'' +
                    ", email='" + email + '\'' +
                    ", password='" + password + '\'' +
                    '}';
        }
    }

    public static class RegisterResult {
        public Boolean error;
        public String message;
    }

    public static class Notify {
    }
}
