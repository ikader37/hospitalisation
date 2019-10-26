/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
@Table(name = "SALLE")
@NamedQueries({
    @NamedQuery(name = "Salle.findAll", query = "SELECT s FROM Salle s"),
    @NamedQuery(name = "Salle.findByIdsalle", query = "SELECT s FROM Salle s WHERE s.idsalle = :idsalle"),
    @NamedQuery(name = "Salle.findByNumero", query = "SELECT s FROM Salle s WHERE s.numero = :numero"),
    @NamedQuery(name = "Salle.findByLibelle", query = "SELECT s FROM Salle s WHERE s.libelle = :libelle")})
public class Salle implements Serializable {

    public static final String seq="salseq";
    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @SequenceGenerator(name=Salle.seq,sequenceName=Salle.seq,initialValue = 1, allocationSize=1)//Cree un generateur de sequence
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator=Salle.seq)//Genere la valeur
    @Basic(optional = false)
    @Column(name = "IDSALLE")
    private BigDecimal idsalle;
    @Column(name = "NUMERO")
    private BigInteger numero;
    @Column(name = "LIBELLE")
    private String libelle;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idsalle")
    private List<Lit> litList;

    public Salle() {
    }

    public Salle(BigDecimal idsalle) {
        this.idsalle = idsalle;
    }

    public BigDecimal getIdsalle() {
        return idsalle;
    }

    public void setIdsalle(BigDecimal idsalle) {
        this.idsalle = idsalle;
    }

    public BigInteger getNumero() {
        return numero;
    }

    public void setNumero(BigInteger numero) {
        this.numero = numero;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public List<Lit> getLitList() {
        return litList;
    }

    public void setLitList(List<Lit> litList) {
        this.litList = litList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idsalle != null ? idsalle.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Salle)) {
            return false;
        }
        Salle other = (Salle) object;
        if ((this.idsalle == null && other.idsalle != null) || (this.idsalle != null && !this.idsalle.equals(other.idsalle))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Salle[ idsalle=" + idsalle + " ]";
    }
    
    //Enregistrer une salle dans la base de donnees
    
    public void save(){
        try {
            utilities.Utils.saljpa.create(this);
            JOptionPane.showMessageDialog(null, "Reussit!!Enregistrement effectué qve success");
            System.out.println("OK");
        } catch (Exception e) {
            System.out.println("NON "+e.getLocalizedMessage());
            JOptionPane.showMessageDialog(null, "Erreur!!Veuillez reesayer");
        }
    }
    
    
    //Mettre a jour une salle
    public void update(){
        try {
            utilities.Utils.saljpa.edit(this);
            JOptionPane.showMessageDialog(null, "Reussit!!Mise a jour effectuee avec sucess");
        System.out.println("OK");
        } catch (Exception e) {
            System.out.println("NON "+e.getLocalizedMessage());
            JOptionPane.showMessageDialog(null, "Erreur!!Veuillez reesayer");
        }
    }
    
    //Supprimer une salle de la base de donnees
    public void delete(){
        try {
            utilities.Utils.saljpa.destroy(idsalle);
             JOptionPane.showMessageDialog(null, "Reussit!!Suppression reussit");
            System.out.println("OK");
        } catch (Exception e) {
            System.out.println("NON "+e.getLocalizedMessage());
             JOptionPane.showMessageDialog(null, "Erreur!!Veuillez reesayer");
        }
    }
    
    public Salle salleByLib(String lib){
        String sql="select idsalle from salle where numero ='"+lib+"'";
        Salle sal=new Salle();
        
       Connection con= SingletonConnection.getConnecter();
        try {
            Statement st=con.createStatement();
            
            ResultSet rs=st.executeQuery(sql);
            rs.next();
            sal=utilities.Utils.saljpa.findSalle(rs.getBigDecimal("idsalle"));
        } catch (Exception e) {
        }
        return sal;
    }
}
