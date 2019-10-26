/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;import javax.persistence.OneToMany;

import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import utilities.SingletonConnection;

/**
 *
 * @author DELL
 */
@Entity
@Table(name = "HOSPITALISER")
@NamedQueries({
    @NamedQuery(name = "Hospitaliser.findAll", query = "SELECT h FROM Hospitaliser h"),
    @NamedQuery(name = "Hospitaliser.findByIdetudiant", query = "SELECT h FROM Hospitaliser h WHERE h.hospitaliserPK.idetudiant = :idetudiant"),
    @NamedQuery(name = "Hospitaliser.findByIdlit", query = "SELECT h FROM Hospitaliser h WHERE h.hospitaliserPK.idlit = :idlit"),
    @NamedQuery(name = "Hospitaliser.findByIdhosp", query = "SELECT h FROM Hospitaliser h WHERE h.hospitaliserPK.idhosp = :idhosp"),
    @NamedQuery(name = "Hospitaliser.findByDateentre", query = "SELECT h FROM Hospitaliser h WHERE h.dateentre = :dateentre"),
    @NamedQuery(name = "Hospitaliser.findByDatesortie", query = "SELECT h FROM Hospitaliser h WHERE h.datesortie = :datesortie"),
    @NamedQuery(name = "Hospitaliser.findByCommentaire", query = "SELECT h FROM Hospitaliser h WHERE h.commentaire = :commentaire")})
public class Hospitaliser implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected HospitaliserPK hospitaliserPK;
    @Column(name = "DATEENTRE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateentre;
    @Column(name = "DATESORTIE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datesortie;
    @Column(name = "COMMENTAIRE")
    private String commentaire;
    @Column(name = "COMMENTAIREENTREE")
    private String commentaireentree;
    @JoinColumn(name = "IDETUDIANT", referencedColumnName = "IDETUDIANT", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Etudiant etudiant;
    @JoinColumn(name = "IDLIT", referencedColumnName = "IDLIT", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Lit lit;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "hospitaliser")
    private List<Ordonnance> ordonnanceList;

    public Hospitaliser() {
    }

    public Hospitaliser(HospitaliserPK hospitaliserPK) {
        this.hospitaliserPK = hospitaliserPK;
    }

    public Hospitaliser(BigInteger idetudiant, BigInteger idlit, BigInteger idhosp) {
        this.hospitaliserPK = new HospitaliserPK(idetudiant, idlit, idhosp);
    }

    public HospitaliserPK getHospitaliserPK() {
        return hospitaliserPK;
    }

    public void setHospitaliserPK(HospitaliserPK hospitaliserPK) {
        this.hospitaliserPK = hospitaliserPK;
    }

    public Date getDateentre() {
        return dateentre;
    }

    public void setDateentre(Date dateentre) {
        this.dateentre = dateentre;
    }

    public Date getDatesortie() {
        return datesortie;
    }

    public void setDatesortie(Date datesortie) {
        this.datesortie = datesortie;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public String getCommentaireentree() {
        return commentaireentree;
    }

    public void setCommentaireentree(String commentaireentree) {
        this.commentaireentree = commentaireentree;
    }

    public List<Ordonnance> getOrdonnanceList() {
        return ordonnanceList;
    }

    public void setOrdonnanceList(List<Ordonnance> ordonnanceList) {
        this.ordonnanceList = ordonnanceList;
    }

    
    
    
    public Etudiant getEtudiant() {
        return etudiant;
    }

    public void setEtudiant(Etudiant etudiant) {
        this.etudiant = etudiant;
    }

    public Lit getLit() {
        return lit;
    }

    public void setLit(Lit lit) {
        this.lit = lit;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (hospitaliserPK != null ? hospitaliserPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Hospitaliser)) {
            return false;
        }
        Hospitaliser other = (Hospitaliser) object;
        if ((this.hospitaliserPK == null && other.hospitaliserPK != null) || (this.hospitaliserPK != null && !this.hospitaliserPK.equals(other.hospitaliserPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Hospitaliser[ hospitaliserPK=" + hospitaliserPK + " ]";
    }
    
    
    
    //Cette fonction retroune la liste des hospitalises
    public List<Hospitaliser> listHospi(){
        
        String sql="select h.idhosp as idh,e.idetudiant as idet,e.nom as nom,e.prenom as prenom,l.idlit as idlit,l.code as code,s.idsalle as idsalle,s.libelle as salle from hospitaliser h,etudiant e,lit l,salle s where h.idetudiant=e.idetudiant and h.idlit=l.idlit and l.idsalle=s.idsalle and sortie=0";
        Connection c=SingletonConnection.getConnecter();
        List<Hospitaliser> idh=new ArrayList<>();
        
        try {
            
            Statement st=c.createStatement();
            ResultSet rs=st.executeQuery(sql);
            //Parcourons la liste pour recuperer les elemets
            while(rs.next()){
                Etudiant edt=utilities.Utils.edtjpa.findEtudiant(rs.getBigDecimal("idet"));
//                
//                edt.setNom(rs.getString("nom"));
//                edt.setPrenom(rs.getString("prenom"));
                
                Lit l=utilities.Utils.litjpa.findLit(rs.getBigDecimal("idlit"));
                
                //l.setCode(rs.getString("code"));
                Salle s=utilities.Utils.saljpa.findSalle(rs.getBigDecimal("idsalle"));
                
                //s.setLibelle(rs.getString("salle"));
                //l.setIdsalle(s);
                
                HospitaliserPK pk=new HospitaliserPK();
                pk.setIdhosp(rs.getBigDecimal("idh").toBigInteger());
//                pk.setIdetudiant(edt.getIdetudiant().toBigInteger());
//                pk.setIdlit(l.getIdlit().toBigInteger());
                
                Hospitaliser h=new Hospitaliser();
                
                h.setLit(l);
                h.setEtudiant(edt);
                
                h.setHospitaliserPK(pk);
                idh.add(h);
            }
        } catch (Exception e) {
        }
        return idh;
    }
    
    //Permer d'avoir le nombre d'hospitabliser
    public int nombreHosp(){
        
        String sql="select count(idhosp) from hospitaliser where sortie =0";
        
        return 1;
    }
}
