/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.math.BigDecimal;
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
import utilities.SingletonConnection;

/**
 *
 * @author DELL
 */
@Entity
@Table(name = "FILIERE")
@NamedQueries({
    @NamedQuery(name = "Filiere.findAll", query = "SELECT f FROM Filiere f"),
    @NamedQuery(name = "Filiere.findByIdfiliere", query = "SELECT f FROM Filiere f WHERE f.idfiliere = :idfiliere"),
    @NamedQuery(name = "Filiere.findByLibelle", query = "SELECT f FROM Filiere f WHERE f.libelle = :libelle")})
public class Filiere implements Serializable {

    private static final String seq="filseq";
    
    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @Basic(optional = false)
    @SequenceGenerator(name=Filiere.seq,sequenceName=Filiere.seq,initialValue = 1, allocationSize=1)//Cree un generateur de sequence
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator=Filiere.seq)//Genere la valeur 
    @Column(name = "IDFILIERE")
    private BigDecimal idfiliere;
    @Column(name = "LIBELLE")
    private String libelle;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idfiliere")
    private List<Etudiant> etudiantList;

    public Filiere() {
    }

    public Filiere(BigDecimal idfiliere) {
        this.idfiliere = idfiliere;
    }

    public BigDecimal getIdfiliere() {
        return idfiliere;
    }

    public void setIdfiliere(BigDecimal idfiliere) {
        this.idfiliere = idfiliere;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public List<Etudiant> getEtudiantList() {
        return etudiantList;
    }

    public void setEtudiantList(List<Etudiant> etudiantList) {
        this.etudiantList = etudiantList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idfiliere != null ? idfiliere.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Filiere)) {
            return false;
        }
        Filiere other = (Filiere) object;
        if ((this.idfiliere == null && other.idfiliere != null) || (this.idfiliere != null && !this.idfiliere.equals(other.idfiliere))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Filiere[ idfiliere=" + idfiliere + " ]";
    }
    
    public Filiere filiereByLib(String lib){
        String sql="select idfiliere from filiere where libelle ='"+lib+"'";
        Filiere fil=new Filiere();
        
       Connection con= SingletonConnection.getConnecter();
        try {
            Statement st=con.createStatement();
            
            ResultSet rs=st.executeQuery(sql);
            rs.next();
            fil=utilities.Utils.fjpa.findFiliere(rs.getBigDecimal("idfiliere"));
        } catch (Exception e) {
        }
        return fil;
       // return null;
    }
    
    public void save(){
        try {
            utilities.Utils.fjpa.create(this);
            utilities.Utils.afficherMessage("Enregistrement reussit!!");
        } catch (Exception e) {
            utilities.Utils.afficherMessage("Erreur !! Enregistrement échoué!");
        }
    }
    public void update()
    {
        try {
            utilities.Utils.fjpa.edit(this);
            utilities.Utils.afficherMessage("Mise à jour reussit!!");
        } catch (Exception e) {
            utilities.Utils.afficherMessage("Erreur !! Mise à jour échouée!");
        }
    }
    
    public void delete(){
        try {
            utilities.Utils.fjpa.destroy(idfiliere);
            utilities.Utils.afficherMessage("Suppression reussit!!");
        } catch (Exception e) {
            utilities.Utils.afficherMessage("Erreur !! Suppression échoué!");
        }
    }
    
}
