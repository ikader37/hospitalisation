package utilities;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author DELL
 */
public class SingletonConnection {
    
    
    private static Connection connecter;
    static{//Sexecute au moment du chargement de la classe en memoire
       try{
        Class.forName("oracle.jdbc.OracleDriver");
        connecter=DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "Gestionnaire","1234");
       }
       catch(ClassNotFoundException e)
       {
           
       } catch (SQLException ex) {
            Logger.getLogger(SingletonConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static Connection getConnecter() {
        System.out.println(connecter);
        
        return connecter;
    }
    
    
    public Connection connecter() 
    {
        try{
            Class.forName("oracle.jdbc.OracleDriver");
            connecter=DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE","Gestionnaire","1234");
            
        }catch(ClassNotFoundException e)
        {
            
        }
        catch (SQLException ex) {
            //Logger.getLogger(Users.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return connecter;
    }   
}
