/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.math.BigInteger;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.SequenceGenerator;

/**
 *
 * @author DELL
 */
@SequenceGenerator(name=Etudiant.seq,sequenceName=Etudiant.seq,initialValue = 1, allocationSize=5)//Cree un generateur de sequence
@Embeddable
public class HospitaliserPK implements Serializable {

    
    public static final String seq="hospseq";
    
    @Basic(optional = false)
    @Column(name = "IDETUDIANT")
    private BigInteger idetudiant;
    @Basic(optional = false)
    @Column(name = "IDLIT")
    private BigInteger idlit;
    @Basic(optional = false)
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator=Etudiant.seq)//Genere la valeur 
    @Column(name = "IDHOSP")
    private BigInteger idhosp;

    public HospitaliserPK() {
    }

    public HospitaliserPK(BigInteger idetudiant, BigInteger idlit, BigInteger idhosp) {
        this.idetudiant = idetudiant;
        this.idlit = idlit;
        this.idhosp = idhosp;
    }

    public BigInteger getIdetudiant() {
        return idetudiant;
    }

    public void setIdetudiant(BigInteger idetudiant) {
        this.idetudiant = idetudiant;
    }

    public BigInteger getIdlit() {
        return idlit;
    }

    public void setIdlit(BigInteger idlit) {
        this.idlit = idlit;
    }

    public BigInteger getIdhosp() {
        return idhosp;
    }

    public void setIdhosp(BigInteger idhosp) {
        this.idhosp = idhosp;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idetudiant != null ? idetudiant.hashCode() : 0);
        hash += (idlit != null ? idlit.hashCode() : 0);
        hash += (idhosp != null ? idhosp.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof HospitaliserPK)) {
            return false;
        }
        HospitaliserPK other = (HospitaliserPK) object;
        if ((this.idetudiant == null && other.idetudiant != null) || (this.idetudiant != null && !this.idetudiant.equals(other.idetudiant))) {
            return false;
        }
        if ((this.idlit == null && other.idlit != null) || (this.idlit != null && !this.idlit.equals(other.idlit))) {
            return false;
        }
        if ((this.idhosp == null && other.idhosp != null) || (this.idhosp != null && !this.idhosp.equals(other.idhosp))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.HospitaliserPK[ idetudiant=" + idetudiant + ", idlit=" + idlit + ", idhosp=" + idhosp + " ]";
    }
    
}
