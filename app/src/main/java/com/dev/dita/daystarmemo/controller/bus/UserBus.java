package com.dev.dita.daystarmemo.controller.bus;


/**
 * A class containing all classes used by the EventBus when dealing with a user
 */
public class UserBus {
    /**
     * The type Login event.
     */
    public static class LoginEvent {
        /**
         * The Username.
         */
        public String username;
        /**
         * The Password.
         */
        public String password;

        /**
         * Instantiates a new Login event.
         *
         * @param username the username
         * @param password the password
         */
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

    /**
     * The type Login result.
     */
    public static class LoginResult {
        /**
         * The Error.
         */
        public Boolean error;
        /**
         * The Message.
         */
        public String message;
    }

    /**
     * The type Logout result.
     */
    public static class LogoutResult {
        /**
         * The Error.
         */
        public Boolean error;
        /**
         * The Message.
         */
        public String message;
    }

    /**
     * The type Register event.
     */
    public static class RegisterEvent {
        /**
         * The Username.
         */
        public String username;
        /**
         * The Email.
         */
        public String email;
        /**
         * The Password.
         */
        public String password;

        /**
         * Instantiates a new Register event.
         *
         * @param username the username
         * @param email    the email
         * @param password the password
         */
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

    /**
     * The type Register result.
     */
    public static class RegisterResult {
        /**
         * The Error.
         */
        public Boolean error;
        /**
         * The Message.
         */
        public String message;
    }

    /**
     * The type Notify.
     */
    public static class Notify {
    }

    /**
     * The type Profile updated event.
     */
    public static class ProfileUpdatedEvent {
    }
}
