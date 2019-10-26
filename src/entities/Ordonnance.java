/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author DELL
 */
@Entity
@Table(name = "ORDONNANCE")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Ordonnance.findAll", query = "SELECT o FROM Ordonnance o"),
    @NamedQuery(name = "Ordonnance.findByIdordonnance", query = "SELECT o FROM Ordonnance o WHERE o.idordonnance = :idordonnance"),
    @NamedQuery(name = "Ordonnance.findByDateOrd", query = "SELECT o FROM Ordonnance o WHERE o.dateOrd = :dateOrd")})
public class Ordonnance implements Serializable {

    
    public static final String seq="ordoseq";
    
    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @Basic(optional = false)
    @Column(name = "IDORDONNANCE")
    @SequenceGenerator(name=Ordonnance.seq,sequenceName=Ordonnance.seq,initialValue = 1, allocationSize=1)//Cree un generateur de sequence
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator=Ordonnance.seq)//Genere la valeur 
    private BigDecimal idordonnance;
    @Column(name = "DATE_ORD")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateOrd;
    @JoinColumns({
        @JoinColumn(name = "IDETUDIANT", referencedColumnName = "IDETUDIANT"),
        @JoinColumn(name = "IDLIT", referencedColumnName = "IDLIT"),
        @JoinColumn(name = "IDHOSP", referencedColumnName = "IDHOSP")})
    @ManyToOne(optional = false)
    private Hospitaliser hospitaliser;

    public Ordonnance() {
    }

    public Ordonnance(BigDecimal idordonnance) {
        this.idordonnance = idordonnance;
    }

    public BigDecimal getIdordonnance() {
        return idordonnance;
    }

    public void setIdordonnance(BigDecimal idordonnance) {
        this.idordonnance = idordonnance;
    }

    public Date getDateOrd() {
        return dateOrd;
    }

    public void setDateOrd(Date dateOrd) {
        this.dateOrd = dateOrd;
    }

    public Hospitaliser getHospitaliser() {
        return hospitaliser;
    }

    public void setHospitaliser(Hospitaliser hospitaliser) {
        this.hospitaliser = hospitaliser;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idordonnance != null ? idordonnance.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Ordonnance)) {
            return false;
        }
        Ordonnance other = (Ordonnance) object;
        if ((this.idordonnance == null && other.idordonnance != null) || (this.idordonnance != null && !this.idordonnance.equals(other.idordonnance))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "dd.Ordonnance[ idordonnance=" + idordonnance + " ]";
    }
    
    public void save(){
        try {
            utilities.Utils.odrdJpa.create(this);
        } catch (Exception e) {
            
        }
    }
}
