/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Date;
import java.util.List;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.swing.JOptionPane;
import utilities.SingletonConnection;

/**
 *
 * @author DELL
 */
@Entity
@Table(name = "ETUDIANT")
@NamedQueries({
    @NamedQuery(name = "Etudiant.findAll", query = "SELECT e FROM Etudiant e"),
    @NamedQuery(name = "Etudiant.findByIdetudiant", query = "SELECT e FROM Etudiant e WHERE e.idetudiant = :idetudiant"),
    @NamedQuery(name = "Etudiant.findByMatricule", query = "SELECT e FROM Etudiant e WHERE e.matricule = :matricule"),
    @NamedQuery(name = "Etudiant.findByNom", query = "SELECT e FROM Etudiant e WHERE e.nom = :nom"),
    @NamedQuery(name = "Etudiant.findByPrenom", query = "SELECT e FROM Etudiant e WHERE e.prenom = :prenom"),
    @NamedQuery(name = "Etudiant.findByCnib", query = "SELECT e FROM Etudiant e WHERE e.cnib = :cnib"),
    @NamedQuery(name = "Etudiant.findByNaissance", query = "SELECT e FROM Etudiant e WHERE e.naissance = :naissance"),
    @NamedQuery(name = "Etudiant.findByPhoto", query = "SELECT e FROM Etudiant e WHERE e.photo = :photo")})
@SequenceGenerator(name=Etudiant.seq,sequenceName=Etudiant.seq,initialValue = 1, allocationSize=1)//Cree un generateur de sequence
public class Etudiant implements Serializable {

    
    public static final String seq="litseq";
    
    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @Basic(optional = false)
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator=Etudiant.seq)//Genere la valeur 
    @Column(name = "IDETUDIANT")
    private BigDecimal idetudiant;
    @Column(name = "MATRICULE")
    private String matricule;
    @Column(name = "NOM")
    private String nom;
    @Column(name = "PRENOM")
    private String prenom;
    @Column(name = "CNIB")
    private String cnib;
    @Column(name = "NAISSANCE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date naissance;
    @Column(name = "PHOTO")
    private Serializable photo;
    @JoinColumn(name = "IDFILIERE", referencedColumnName = "IDFILIERE")
    @ManyToOne(optional = false)
    private Filiere idfiliere;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "etudiant")
    private List<Hospitaliser> hospitaliserList;

    public Etudiant() {
    }

    public Etudiant(BigDecimal idetudiant) {
        this.idetudiant = idetudiant;
    }

    public BigDecimal getIdetudiant() {
        return idetudiant;
    }

    public void setIdetudiant(BigDecimal idetudiant) {
        this.idetudiant = idetudiant;
    }

    public String getMatricule() {
        return matricule;
    }

    public void setMatricule(String matricule) {
        this.matricule = matricule;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getCnib() {
        return cnib;
    }

    public void setCnib(String cnib) {
        this.cnib = cnib;
    }

    public Date getNaissance() {
        return naissance;
    }

    public void setNaissance(Date naissance) {
        this.naissance = naissance;
    }

    public Serializable getPhoto() {
        return photo;
    }

    public void setPhoto(Serializable photo) {
        this.photo = photo;
    }

    public Filiere getIdfiliere() {
        return idfiliere;
    }

    public void setIdfiliere(Filiere idfiliere) {
        this.idfiliere = idfiliere;
    }

    public List<Hospitaliser> getHospitaliserList() {
        return hospitaliserList;
    }

    public void setHospitaliserList(List<Hospitaliser> hospitaliserList) {
        this.hospitaliserList = hospitaliserList;
    }

//    @Override
//    public int hashCode() {
//        int hash = 0;
//        hash += (idetudiant != null ? idetudiant.hashCode() : 0);
//        return hash;
//    }

//    @Override
//    public boolean equals(Object object) {
//        // TODO: Warning - this method won't work in the case the id fields are not set
//        if (!(object instanceof Etudiant)) {
//            return false;
//        }
//        Etudiant other = (Etudiant) object;
//        if ((this.idetudiant == null && other.idetudiant != null) || (this.idetudiant != null && !this.idetudiant.equals(other.idetudiant))) {
//            return false;
//        }
//        return true;
//    }

    @Override
    public String toString() {
        return "entities.Etudiant[ idetudiant=" + idetudiant + " ]";
    }
    
    
    public void save(Hospitaliser h){
        try {
            
            if(h.getLit().litbusy()){
                System.out.println("OQP");
            }else{
                System.out.println("NON OQP");
            }
            utilities.Utils.edtjpa.create(this);
            String sql="insert into ETUDIANT(idetudiant,idfiliere,nom,prenom,matricule)"+
                    " values(?,?,?,?,?)";//select etudiantseq.nextval from dual
            
            Connection c=SingletonConnection.getConnecter();
            
            PreparedStatement ps=c.prepareStatement(sql);
            ps.setBigDecimal(1, idetudiant);
            ps.setBigDecimal(2, idfiliere.getIdfiliere());
            ps.setString(3, nom);
            ps.setString(4, prenom);
            ps.setString(5, matricule);
            //ps.setByte(5, photo);
//           boolean v=ps.execute();
//          System.out.println("OK "+v );
          
          sql="insert into hospitaliser (idhosp,idlit,idetudiant)"+
                  " values(?,?,?)";
            PreparedStatement p=c.prepareStatement(sql);
            p.setBigDecimal(1,BigDecimal.valueOf(22));
            p.setBigDecimal(2, h.getLit().getIdlit());
            p.setBigDecimal(3, idetudiant);
            //p.execute();
            h.setEtudiant(this);
            HospitaliserPK pk=new HospitaliserPK();
            pk.setIdlit(h.getLit().getIdlit().toBigInteger());
            pk.setIdetudiant(idetudiant.toBigInteger());
            h.setHospitaliserPK(pk);
            h.setDateentre(new Date());
            utilities.Utils.hjpa.create(h);
            JOptionPane.showMessageDialog(null, "Reussit!!Enregistrement effectué avec success");
            
            System.out.println("OK");
        } catch (Exception e) {
            System.out.println("NON :"+e.getMessage());
            JOptionPane.showMessageDialog(null, "Erreur!!Enregistrement échouée");
        }
        
        System.out.println("ID :"+idetudiant);
    }
}
