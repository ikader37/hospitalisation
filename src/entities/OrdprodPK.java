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

/**
 *
 * @author DELL
 */
@Embeddable
public class OrdprodPK implements Serializable {

    @Basic(optional = false)
    @Column(name = "IDORDONNANCE")
    private BigInteger idordonnance;
    @Basic(optional = false)
    @Column(name = "IDPRODUIT")
    private BigInteger idproduit;

    public OrdprodPK() {
    }

    public OrdprodPK(BigInteger idordonnance, BigInteger idproduit) {
        this.idordonnance = idordonnance;
        this.idproduit = idproduit;
    }

    public BigInteger getIdordonnance() {
        return idordonnance;
    }

    public void setIdordonnance(BigInteger idordonnance) {
        this.idordonnance = idordonnance;
    }

    public BigInteger getIdproduit() {
        return idproduit;
    }

    public void setIdproduit(BigInteger idproduit) {
        this.idproduit = idproduit;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idordonnance != null ? idordonnance.hashCode() : 0);
        hash += (idproduit != null ? idproduit.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof OrdprodPK)) {
            return false;
        }
        OrdprodPK other = (OrdprodPK) object;
        if ((this.idordonnance == null && other.idordonnance != null) || (this.idordonnance != null && !this.idordonnance.equals(other.idordonnance))) {
            return false;
        }
        if ((this.idproduit == null && other.idproduit != null) || (this.idproduit != null && !this.idproduit.equals(other.idproduit))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "dd.OrdprodPK[ idordonnance=" + idordonnance + ", idproduit=" + idproduit + " ]";
    }
    
}
