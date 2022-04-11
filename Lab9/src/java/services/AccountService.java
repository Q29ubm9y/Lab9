package services;

import dataaccess.UserDB;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.User;

public class AccountService {
    
    public User login(String email, String password) {
        UserDB userDB = new UserDB();
        
        try {
            User user = userDB.get(email);
            if (password.equals(user.getPassword())) {
                return user;
            }
        } catch (Exception e) {
        }
        
        return null;
    }
    
    public void resetPassword(String email, String path, String url) {
        try {
            String uuid = UUID.randomUUID().toString();
            String link = url + "?uuid=" + uuid;
            UserDB userDB = new UserDB();
            User user = userDB.get(email);
            user.setResetPasswordUuid(uuid);
            userDB.update(user);
            
            String to = user.getEmail();
            String subject = "Notes App Reset Password";
            String template = path + "/emailtemplates/resetpassword.html";
            
            HashMap<String, String> tags = new HashMap<>();
            tags.put("firstname", user.getFirstName());
            tags.put("lastname", user.getLastName());
            tags.put("link", link);
            
            GmailService.sendMail(to, subject, template, tags);
        } catch (Exception ex) {
        }
    }
    
    public boolean changePassword(String uuid, String password) {
        UserDB userDB = new UserDB();
        try {
            User user = userDB.getByUUID(uuid);
            user.setPassword(password);
            user.setResetPasswordUuid(null);
            userDB.update(user);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
