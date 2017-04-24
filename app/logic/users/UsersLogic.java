package logic.users;

import models.users.User;

/**
 * Created by Esteban Luchsinger on 04.04.2017.
 * Handles the user problem domain.
 */
public interface UsersLogic {
    /**
     * Gets a user by it's id.
     * @param userId The unique UserID of the user to get.
     * @return Returns the found user or null, if nothing was found.
     */
    User getUserById(Integer userId);

    /**
     * Registers a new user.
     * @param user The new user to register.
     */
    void registerUser(User user);

    /**
     * Returns a user by it's email.
     * @param email The email of the user to get.
     * @return Returns the found user or null, if nothing was found.
     */
    User getUserByEmail(String email);
}
