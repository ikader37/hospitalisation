/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.math.BigInteger;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author DELL
 */
@Entity
@Table(name = "ORDPROD")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Ordprod.findAll", query = "SELECT o FROM Ordprod o"),
    @NamedQuery(name = "Ordprod.findByIdordonnance", query = "SELECT o FROM Ordprod o WHERE o.ordprodPK.idordonnance = :idordonnance"),
    @NamedQuery(name = "Ordprod.findByIdproduit", query = "SELECT o FROM Ordprod o WHERE o.ordprodPK.idproduit = :idproduit")})
public class Ordprod implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected OrdprodPK ordprodPK;
    @JoinColumn(name = "IDPRODUIT", referencedColumnName = "IDPRODUIT", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Produit produit;

    public Ordprod() {
    }

    public Ordprod(OrdprodPK ordprodPK) {
        this.ordprodPK = ordprodPK;
    }

    public Ordprod(BigInteger idordonnance, BigInteger idproduit) {
        this.ordprodPK = new OrdprodPK(idordonnance, idproduit);
    }

    public OrdprodPK getOrdprodPK() {
        return ordprodPK;
    }

    public void setOrdprodPK(OrdprodPK ordprodPK) {
        this.ordprodPK = ordprodPK;
    }

    public Produit getProduit() {
        return produit;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (ordprodPK != null ? ordprodPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Ordprod)) {
            return false;
        }
        Ordprod other = (Ordprod) object;
        if ((this.ordprodPK == null && other.ordprodPK != null) || (this.ordprodPK != null && !this.ordprodPK.equals(other.ordprodPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "dd.Ordprod[ ordprodPK=" + ordprodPK + " ]";
    }
    
    public void save(){
        try {
            utilities.Utils.orJp.create(this);
        } catch (Exception e) {
        }
    }
}
