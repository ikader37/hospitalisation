/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entities.Ordprod;
import entities.Produit;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import models.exceptions.IllegalOrphanException;
import models.exceptions.NonexistentEntityException;
import models.exceptions.PreexistingEntityException;

/**
 *
 * @author DELL
 */
public class ProduitJpaController implements Serializable {

    public ProduitJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Produit produit) throws PreexistingEntityException, Exception {
        if (produit.getOrdprodList() == null) {
            produit.setOrdprodList(new ArrayList<Ordprod>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Ordprod> attachedOrdprodList = new ArrayList<Ordprod>();
            for (Ordprod ordprodListOrdprodToAttach : produit.getOrdprodList()) {
                ordprodListOrdprodToAttach = em.getReference(ordprodListOrdprodToAttach.getClass(), ordprodListOrdprodToAttach.getOrdprodPK());
                attachedOrdprodList.add(ordprodListOrdprodToAttach);
            }
            produit.setOrdprodList(attachedOrdprodList);
            em.persist(produit);
            for (Ordprod ordprodListOrdprod : produit.getOrdprodList()) {
                Produit oldProduitOfOrdprodListOrdprod = ordprodListOrdprod.getProduit();
                ordprodListOrdprod.setProduit(produit);
                ordprodListOrdprod = em.merge(ordprodListOrdprod);
                if (oldProduitOfOrdprodListOrdprod != null) {
                    oldProduitOfOrdprodListOrdprod.getOrdprodList().remove(ordprodListOrdprod);
                    oldProduitOfOrdprodListOrdprod = em.merge(oldProduitOfOrdprodListOrdprod);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findProduit(produit.getIdproduit()) != null) {
                throw new PreexistingEntityException("Produit " + produit + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Produit produit) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Produit persistentProduit = em.find(Produit.class, produit.getIdproduit());
            List<Ordprod> ordprodListOld = persistentProduit.getOrdprodList();
            List<Ordprod> ordprodListNew = produit.getOrdprodList();
            List<String> illegalOrphanMessages = null;
            for (Ordprod ordprodListOldOrdprod : ordprodListOld) {
                if (!ordprodListNew.contains(ordprodListOldOrdprod)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Ordprod " + ordprodListOldOrdprod + " since its produit field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Ordprod> attachedOrdprodListNew = new ArrayList<Ordprod>();
            for (Ordprod ordprodListNewOrdprodToAttach : ordprodListNew) {
                ordprodListNewOrdprodToAttach = em.getReference(ordprodListNewOrdprodToAttach.getClass(), ordprodListNewOrdprodToAttach.getOrdprodPK());
                attachedOrdprodListNew.add(ordprodListNewOrdprodToAttach);
            }
            ordprodListNew = attachedOrdprodListNew;
            produit.setOrdprodList(ordprodListNew);
            produit = em.merge(produit);
            for (Ordprod ordprodListNewOrdprod : ordprodListNew) {
                if (!ordprodListOld.contains(ordprodListNewOrdprod)) {
                    Produit oldProduitOfOrdprodListNewOrdprod = ordprodListNewOrdprod.getProduit();
                    ordprodListNewOrdprod.setProduit(produit);
                    ordprodListNewOrdprod = em.merge(ordprodListNewOrdprod);
                    if (oldProduitOfOrdprodListNewOrdprod != null && !oldProduitOfOrdprodListNewOrdprod.equals(produit)) {
                        oldProduitOfOrdprodListNewOrdprod.getOrdprodList().remove(ordprodListNewOrdprod);
                        oldProduitOfOrdprodListNewOrdprod = em.merge(oldProduitOfOrdprodListNewOrdprod);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                BigDecimal id = produit.getIdproduit();
                if (findProduit(id) == null) {
                    throw new NonexistentEntityException("The produit with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(BigDecimal id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Produit produit;
            try {
                produit = em.getReference(Produit.class, id);
                produit.getIdproduit();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The produit with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Ordprod> ordprodListOrphanCheck = produit.getOrdprodList();
            for (Ordprod ordprodListOrphanCheckOrdprod : ordprodListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Produit (" + produit + ") cannot be destroyed since the Ordprod " + ordprodListOrphanCheckOrdprod + " in its ordprodList field has a non-nullable produit field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(produit);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Produit> findProduitEntities() {
        return findProduitEntities(true, -1, -1);
    }

    public List<Produit> findProduitEntities(int maxResults, int firstResult) {
        return findProduitEntities(false, maxResults, firstResult);
    }

    private List<Produit> findProduitEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Produit.class));
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

    public Produit findProduit(BigDecimal id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Produit.class, id);
        } finally {
            em.close();
        }
    }

    public int getProduitCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Produit> rt = cq.from(Produit.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
