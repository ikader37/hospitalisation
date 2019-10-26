/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.swing.JOptionPane;
import utilities.SingletonConnection;

/**
 *
 * @author DELL
 */
@Entity
@Table(name = "LIT")
@NamedQueries({
    @NamedQuery(name = "Lit.findAll", query = "SELECT l FROM Lit l"),
    @NamedQuery(name = "Lit.findByIdlit", query = "SELECT l FROM Lit l WHERE l.idlit = :idlit"),
    @NamedQuery(name = "Lit.findByCode", query = "SELECT l FROM Lit l WHERE l.code = :code"),
    @NamedQuery(name = "Lit.findByPhoto", query = "SELECT l FROM Lit l WHERE l.photo = :photo")})
public class Lit implements Serializable {

    public static final String seq="litseq";//Defini le nom de la sequence
    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @SequenceGenerator(name=Lit.seq,sequenceName=Lit.seq,initialValue = 1, allocationSize=1)//Cree un generateur de sequence
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator=Lit.seq)//Genere la valeur 
    @Basic(optional = false)
    @Column(name = "IDLIT")
    private BigDecimal idlit;
    @Column(name = "CODE")
    private String code;
    @Column(name = "PHOTO")
    private byte[] photo;
    @JoinColumn(name = "IDSALLE", referencedColumnName = "IDSALLE")
    @ManyToOne(optional = false)
    private Salle idsalle;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "lit")
    private List<Hospitaliser> hospitaliserList;

    public Lit() {
    }

    public Lit(BigDecimal idlit) {
        this.idlit = idlit;
    }

    public BigDecimal getIdlit() {
        return idlit;
    }

    public void setIdlit(BigDecimal idlit) {
        this.idlit = idlit;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public Salle getIdsalle() {
        return idsalle;
    }

    public void setIdsalle(Salle idsalle) {
        this.idsalle = idsalle;
    }

    public List<Hospitaliser> getHospitaliserList() {
        return hospitaliserList;
    }

    public void setHospitaliserList(List<Hospitaliser> hospitaliserList) {
        this.hospitaliserList = hospitaliserList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idlit != null ? idlit.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Lit)) {
            return false;
        }
        Lit other = (Lit) object;
        if ((this.idlit == null && other.idlit != null) || (this.idlit != null && !this.idlit.equals(other.idlit))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Lit[ idlit=" + idlit + " ]";
    }
    
    
    //enregistrer dans la base de donnees
    public void save(){
        try {
           // utilities.Utils.litjpa.create(this);
           String sql="insert into lit(idlit,idsalle,code,photo) values(?,?,?,?)";
           String sql2="select litseq.nextval from dual";
            Connection c=SingletonConnection.getConnecter();
            Statement st=c.createStatement();
            ResultSet rs=st.executeQuery(sql2);
            rs.next();
            BigDecimal lid=rs.getBigDecimal(1);//Recuperer la valeur de la sequence
            
            PreparedStatement ps=c.prepareStatement(sql);
            ps.setBigDecimal(1, lid);
            ps.setBigDecimal(2, idsalle.getIdsalle());
            ps.setString(3, code);
            ps.setBytes(4, photo);
            ps.execute();
           
            System.out.println("OK");
            JOptionPane.showMessageDialog(null, "Reussit!!Enregistrement reussit");

        } catch (Exception e) {
            System.out.println("NON   :"+e.getLocalizedMessage());
            JOptionPane.showMessageDialog(null, "Erreur!!Enregistrement éhouée. Veuillez réessayer");
        }
    }
    
    public void delete(){
        try {
            
            utilities.Utils.litjpa.destroy(idlit);
            JOptionPane.showMessageDialog(null, "Reussit!!Suppression reussit");
            
            System.out.println("OK");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erreur!!Suppression echouée");
            System.out.println("NON   :"+e.getLocalizedMessage());
        }
    }
    
    public void update(){
        try {
            utilities.Utils.litjpa.edit(this);
            System.out.println("OK");
            JOptionPane.showMessageDialog(null, "Reussit!!Mise à jour reussit");
        } catch (Exception e) {
            System.out.println("NON   :"+e.getLocalizedMessage());
            JOptionPane.showMessageDialog(null, "Erreur!!Mise à jour échouée");
        }
    }
    
    public Lit litByCode(String code){
        
        Lit l=new Lit();
        String sql="select idlit from lit where code ='"+code+"'";
        //L sal=new Salle();
        
       Connection con= SingletonConnection.getConnecter();
        try {
            Statement st=con.createStatement();
            
            ResultSet rs=st.executeQuery(sql);
            rs.next();
            l=utilities.Utils.litjpa.findLit(rs.getBigDecimal("idlit"));
            System.out.println("####:"+l.getIdlit());
        } catch (Exception e) {
        }
        return l;
    }
    
    public boolean litbusy(){
        boolean etat = false;
        
        String sql="select count(idlit) as nbre from hospitaliser where idlit='"+idlit+"' and sortie=0";
        
        try {
            Connection c=SingletonConnection.getConnecter();
            
            Statement st=c.createStatement();
           ResultSet rs=st.executeQuery(sql);
           rs.next();
           if(rs.getInt("nbre")>0){
               etat=true;
           }else{
               etat=false;
           }
        } catch (Exception e) {
        }
        return etat;
    }
    
    //Permet de retrouver l'etudiant hhospitalise qui occupe ce lit
    public Etudiant hospitalise(){
        
        String sql="select e.matricule,e.nom,e.prenom, e.photo from hospitaliser h,etudiant e where h.idlit='"+idlit+"' and h.sortie =0";
        Etudiant edt=new Etudiant();
        
        Connection c=SingletonConnection.getConnecter();
        try {
            Statement st=c.createStatement();
            ResultSet rs=st.executeQuery(sql);
            rs.next();
            edt.setMatricule(rs.getString(1));
            edt.setNom(rs.getString(2));
            edt.setPrenom(rs.getString(3));
            edt.setPhoto(rs.getBytes(4));
            
        } catch (Exception e) {
        }
        return edt;
    }
    
    /**
     * 
     * Cette methode permet de retrouver les lits qui sont disponibles
     * @return 
     */
    public List<Lit> litLibre(){
        List<Lit> lt=new ArrayList<>();
        String sql="select l.idlit from lit l where l.idlit not in (select idlit from hospitaliser where sortie=0)";
         try {
            Connection c=SingletonConnection.getConnecter();
            
            Statement st=c.createStatement();
           ResultSet rs=st.executeQuery(sql);
           while(rs.next()){
               Lit li=new Lit();
               li=utilities.Utils.litjpa.findLit(rs.getBigDecimal("idlit"));
               lt.add(li);
           }
         }catch(Exception ex){
             
         }
        
        
        return lt;
    }
    
    public List<Lit> litSalle(BigDecimal salle){
        
        
        List<Lit> li=new ArrayList<>();
        System.err.println("DD :"+salle);
        String sql="select idlit from lit where idsalle ="+salle.toBigInteger();
        Connection c=SingletonConnection.getConnecter();
        
        Statement pst;
        try {
            pst = c.createStatement();
           
            
            ResultSet rs=pst.executeQuery(sql);
            while(rs.next()){
                Lit l=utilities.Utils.litjpa.findLit(rs.getBigDecimal("idlit"));
                System.out.println("LIT :"+l.getCode());
                li.add(l);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Lit.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        return li;
    }
}
