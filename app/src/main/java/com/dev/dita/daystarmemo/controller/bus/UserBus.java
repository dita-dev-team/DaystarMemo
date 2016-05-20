package com.dev.dita.daystarmemo.controller.bus;


/**
 * A class containing all classes used by the EventBus when dealing with a user
 */
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

    public static class LogoutResult {
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

    public static class ProfileUpdatedEvent {
    }
}
