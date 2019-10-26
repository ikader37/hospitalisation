/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import entities.Ordprod;
import entities.OrdprodPK;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entities.Produit;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import models.exceptions.NonexistentEntityException;
import models.exceptions.PreexistingEntityException;

/**
 *
 * @author DELL
 */
public class OrdprodJpaController implements Serializable {

    public OrdprodJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Ordprod ordprod) throws PreexistingEntityException, Exception {
        if (ordprod.getOrdprodPK() == null) {
            ordprod.setOrdprodPK(new OrdprodPK());
        }
        ordprod.getOrdprodPK().setIdproduit(ordprod.getProduit().getIdproduit().toBigInteger());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Produit produit = ordprod.getProduit();
            if (produit != null) {
                produit = em.getReference(produit.getClass(), produit.getIdproduit());
                ordprod.setProduit(produit);
            }
            em.persist(ordprod);
            if (produit != null) {
                produit.getOrdprodList().add(ordprod);
                produit = em.merge(produit);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findOrdprod(ordprod.getOrdprodPK()) != null) {
                throw new PreexistingEntityException("Ordprod " + ordprod + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Ordprod ordprod) throws NonexistentEntityException, Exception {
        ordprod.getOrdprodPK().setIdproduit(ordprod.getProduit().getIdproduit().toBigInteger());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Ordprod persistentOrdprod = em.find(Ordprod.class, ordprod.getOrdprodPK());
            Produit produitOld = persistentOrdprod.getProduit();
            Produit produitNew = ordprod.getProduit();
            if (produitNew != null) {
                produitNew = em.getReference(produitNew.getClass(), produitNew.getIdproduit());
                ordprod.setProduit(produitNew);
            }
            ordprod = em.merge(ordprod);
            if (produitOld != null && !produitOld.equals(produitNew)) {
                produitOld.getOrdprodList().remove(ordprod);
                produitOld = em.merge(produitOld);
            }
            if (produitNew != null && !produitNew.equals(produitOld)) {
                produitNew.getOrdprodList().add(ordprod);
                produitNew = em.merge(produitNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                OrdprodPK id = ordprod.getOrdprodPK();
                if (findOrdprod(id) == null) {
                    throw new NonexistentEntityException("The ordprod with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(OrdprodPK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Ordprod ordprod;
            try {
                ordprod = em.getReference(Ordprod.class, id);
                ordprod.getOrdprodPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The ordprod with id " + id + " no longer exists.", enfe);
            }
            Produit produit = ordprod.getProduit();
            if (produit != null) {
                produit.getOrdprodList().remove(ordprod);
                produit = em.merge(produit);
            }
            em.remove(ordprod);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Ordprod> findOrdprodEntities() {
        return findOrdprodEntities(true, -1, -1);
    }

    public List<Ordprod> findOrdprodEntities(int maxResults, int firstResult) {
        return findOrdprodEntities(false, maxResults, firstResult);
    }

    private List<Ordprod> findOrdprodEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Ordprod.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Ordprod findOrdprod(OrdprodPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Ordprod.class, id);
        } finally {
            em.close();
        }
    }

    public int getOrdprodCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Ordprod> rt = cq.from(Ordprod.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
