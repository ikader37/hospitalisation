/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import utilities.SingletonConnection;
import java.sql.*;
import java.util.ArrayList;

/**
 *
 * @author DELL
 */
@Entity
@Table(name = "PRODUIT")
@NamedQueries({
    @NamedQuery(name = "Produit.findAll", query = "SELECT p FROM Produit p"),
    @NamedQuery(name = "Produit.findByIdproduit", query = "SELECT p FROM Produit p WHERE p.idproduit = :idproduit"),
    @NamedQuery(name = "Produit.findByLibelle", query = "SELECT p FROM Produit p WHERE p.libelle = :libelle"),
    @NamedQuery(name = "Produit.findByDescription", query = "SELECT p FROM Produit p WHERE p.description = :description"),
    @NamedQuery(name = "Produit.findByDosage", query = "SELECT p FROM Produit p WHERE p.dosage = :dosage"),
    @NamedQuery(name = "Produit.findByVoie", query = "SELECT p FROM Produit p WHERE p.voie = :voie"),
    @NamedQuery(name = "Produit.findByUnitejour", query = "SELECT p FROM Produit p WHERE p.unitejour = :unitejour"),
    @NamedQuery(name = "Produit.findByDatedeb", query = "SELECT p FROM Produit p WHERE p.datedeb = :datedeb"),
    @NamedQuery(name = "Produit.findByDatefin", query = "SELECT p FROM Produit p WHERE p.datefin = :datefin")})
public class Produit implements Serializable {

    
    public static final String seq="prodseq";
    
    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @Basic(optional = false)
    @SequenceGenerator(name=Produit.seq,sequenceName=Produit.seq,initialValue = 1, allocationSize=1)//Cree un generateur de sequence
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator=Produit.seq)//Genere la valeur 
    @Column(name = "IDPRODUIT")
    private BigDecimal idproduit;
    @Column(name = "LIBELLE")
    private String libelle;
    @Column(name = "DESCRIPTION")
    private String description;
    @Column(name = "DOSAGE")
    private String dosage;
    @Column(name = "VOIE")
    private String voie;
    @Column(name = "UNITEJOUR")
    private BigInteger unitejour;
    @Column(name = "DATEDEB")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datedeb;
    @Column(name = "DATEFIN")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datefin;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "produit")
    private List<Ordprod> ordprodList;
    @Column(name = "declasser")
    private Integer declasser;

    public Produit() {
    }

    public Produit(BigDecimal idproduit) {
        this.idproduit = idproduit;
    }

    public BigDecimal getIdproduit() {
        return idproduit;
    }

    public void setIdproduit(BigDecimal idproduit) {
        this.idproduit = idproduit;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getVoie() {
        return voie;
    }

    public void setVoie(String voie) {
        this.voie = voie;
    }

    public BigInteger getUnitejour() {
        return unitejour;
    }

    public void setUnitejour(BigInteger unitejour) {
        this.unitejour = unitejour;
    }

    public Date getDatedeb() {
        return datedeb;
    }

    public void setDatedeb(Date datedeb) {
        this.datedeb = datedeb;
    }

    public Date getDatefin() {
        return datefin;
    }

    public void setDatefin(Date datefin) {
        this.datefin = datefin;
    }

    public List<Ordprod> getOrdprodList() {
        return ordprodList;
    }

    public void setOrdprodList(List<Ordprod> ordprodList) {
        this.ordprodList = ordprodList;
    }

    public Integer getDeclasser() {
        return declasser;
    }

    public void setDeclasser(Integer declasser) {
        this.declasser = declasser;
    }
    
    
    

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idproduit != null ? idproduit.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Produit)) {
            return false;
        }
        Produit other = (Produit) object;
        if ((this.idproduit == null && other.idproduit != null) || (this.idproduit != null && !this.idproduit.equals(other.idproduit))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Produit[ idproduit=" + idproduit + " ]";
    }
    
    public void save(){
        try {
            utilities.Utils.pjpa.create(this);
            utilities.Utils.afficherMessage("Enregistrement réussi");
        } catch (Exception e) {
            utilities.Utils.afficherMessageErreur("Enregistrement échoué.");
        }
    }
    
    public void update(){
        try {
            utilities.Utils.pjpa.edit(this);
            utilities.Utils.afficherMessage("Mise à jour réussie");
        } catch (Exception e) {
            utilities.Utils.afficherMessageErreur("Mise à jour échoué.");
        }
    }
    
    public void delete(){
        try {
            utilities.Utils.pjpa.destroy(idproduit);
            utilities.Utils.afficherMessage("Suppression réussie");
        } catch (Exception e) {
            utilities.Utils.afficherMessageErreur("Suppression échouée.");
        }
    }
    
    
    public List<Produit> produitUtilisable(){
        String sql="select idproduit from produit where declasser =0";
        List<Produit> pl=new ArrayList<>();
        try {
            Connection c=SingletonConnection.getConnecter();
            
            
            Statement st=c.createStatement();
            ResultSet rs=st.executeQuery(sql);
            while(rs.next()){
                Produit p=utilities.Utils.pjpa.findProduit(rs.getBigDecimal("idproduit"));
                pl.add(p);
            }
        } catch (Exception e) {
        }
        return pl;
    }
}
