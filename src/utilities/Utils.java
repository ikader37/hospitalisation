/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import java.awt.Image;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import models.*;
import t2s.son.LecteurTexte;

/**
 *
 * @author DELL
 */
public class Utils {
    
    static EntityManagerFactory emf=Persistence.createEntityManagerFactory("hospitalisationPU");
    public static FiliereJpaController fjpa=new FiliereJpaController(emf);
    public static SalleJpaController saljpa=new SalleJpaController(emf);
    public static LitJpaController litjpa=new LitJpaController(emf);
    public static EtudiantJpaController edtjpa=new EtudiantJpaController(emf);
    public static HospitaliserJpaController hjpa=new HospitaliserJpaController(emf);
    public static ProduitJpaController pjpa=new ProduitJpaController(emf);
    public static OrdprodJpaController orJp=new OrdprodJpaController(emf);
    public static OrdonnanceJpaController odrdJpa=new OrdonnanceJpaController(emf);
    
    
    
    
    
    public static void lireText(String text){
        LecteurTexte txt=new LecteurTexte(text);
        txt.playAll();
    }
    
    public static void afficherMessage(String message){
        JOptionPane.showMessageDialog(null, message,"Succes",JOptionPane.INFORMATION_MESSAGE);
    }
    public static void afficherMessageErreur(String message){
        JOptionPane.showMessageDialog(null,  message,"Erreur", JOptionPane.ERROR_MESSAGE);
    }
    
    public static void recadrerImage(byte[] img,JLabel labPhoto){
        ImageIcon ico=null;
        if(img!=null){
            ico=new ImageIcon(img);
        }
        Image ima=ico.getImage();
        Image new_Im=ima.getScaledInstance(labPhoto.getWidth(), labPhoto.getHeight(), Image.SCALE_SMOOTH);
        ImageIcon new_ico=new ImageIcon(new_Im);
        labPhoto.setIcon(new_ico);
        
    }
    
   
    
}
