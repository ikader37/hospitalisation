/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hospitalisation;

import entities.Hospitaliser;
import t2s.son.LecteurTexte;
import views.AccueilPage;
import views.Connexion;

/**
 *
 * @author DELL
 */
public class Hospitalisation {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
//        LitPage p=new LitPage();
//        p.setVisible(true);
        Hospitaliser h=new Hospitaliser();
//          AccueilPage ac=new AccueilPage();
//          ac.setLocationRelativeTo(null);
//          ac.setVisible(true);
            Connexion conn=new Connexion(null, true);
            conn.setLocationRelativeTo(null);
            conn.setVisible(true);
          
                  

    }
    }
    
