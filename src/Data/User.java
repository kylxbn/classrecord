/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Data;

/**
 *
 * @author orthocube
 */
public class User {
    int id;
    String username;
    String password;
    int caps;
    
    public User(int i, String u, String p, int c)
    {
        id = i;
        username = u;
        password = p;
        caps = c;
    }
    
    public int getID()
    {
        return id;
    }
    
    public String getPassword()
    {
        return password;
    }
    
    public String getUsername()
    {
        return username;
    }
    
    public int getCaps()
    {
        return caps;
    }
}
