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
import entities.Lit;
import entities.Salle;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import models.exceptions.IllegalOrphanException;
import models.exceptions.NonexistentEntityException;
import models.exceptions.PreexistingEntityException;
import utilities.SingletonConnection;

/**
 *
 * @author DELL
 */
public class SalleJpaController implements Serializable {

    public SalleJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Salle salle) throws PreexistingEntityException, Exception {
        if (salle.getLitList() == null) {
            salle.setLitList(new ArrayList<Lit>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Lit> attachedLitList = new ArrayList<Lit>();
            for (Lit litListLitToAttach : salle.getLitList()) {
                litListLitToAttach = em.getReference(litListLitToAttach.getClass(), litListLitToAttach.getIdlit());
                attachedLitList.add(litListLitToAttach);
            }
            salle.setLitList(attachedLitList);
            em.persist(salle);
            for (Lit litListLit : salle.getLitList()) {
                Salle oldIdsalleOfLitListLit = litListLit.getIdsalle();
                litListLit.setIdsalle(salle);
                litListLit = em.merge(litListLit);
                if (oldIdsalleOfLitListLit != null) {
                    oldIdsalleOfLitListLit.getLitList().remove(litListLit);
                    oldIdsalleOfLitListLit = em.merge(oldIdsalleOfLitListLit);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findSalle(salle.getIdsalle()) != null) {
                throw new PreexistingEntityException("Salle " + salle + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Salle salle) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Salle persistentSalle = em.find(Salle.class, salle.getIdsalle());
            List<Lit> litListOld = persistentSalle.getLitList();
            List<Lit> litListNew = salle.getLitList();
            List<String> illegalOrphanMessages = null;
            for (Lit litListOldLit : litListOld) {
                if (!litListNew.contains(litListOldLit)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Lit " + litListOldLit + " since its idsalle field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Lit> attachedLitListNew = new ArrayList<Lit>();
            for (Lit litListNewLitToAttach : litListNew) {
                litListNewLitToAttach = em.getReference(litListNewLitToAttach.getClass(), litListNewLitToAttach.getIdlit());
                attachedLitListNew.add(litListNewLitToAttach);
            }
            litListNew = attachedLitListNew;
            salle.setLitList(litListNew);
            salle = em.merge(salle);
            for (Lit litListNewLit : litListNew) {
                if (!litListOld.contains(litListNewLit)) {
                    Salle oldIdsalleOfLitListNewLit = litListNewLit.getIdsalle();
                    litListNewLit.setIdsalle(salle);
                    litListNewLit = em.merge(litListNewLit);
                    if (oldIdsalleOfLitListNewLit != null && !oldIdsalleOfLitListNewLit.equals(salle)) {
                        oldIdsalleOfLitListNewLit.getLitList().remove(litListNewLit);
                        oldIdsalleOfLitListNewLit = em.merge(oldIdsalleOfLitListNewLit);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                BigDecimal id = salle.getIdsalle();
                if (findSalle(id) == null) {
                    throw new NonexistentEntityException("The salle with id " + id + " no longer exists.");
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
            Salle salle;
            try {
                salle = em.getReference(Salle.class, id);
                salle.getIdsalle();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The salle with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Lit> litListOrphanCheck = salle.getLitList();
            for (Lit litListOrphanCheckLit : litListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Salle (" + salle + ") cannot be destroyed since the Lit " + litListOrphanCheckLit + " in its litList field has a non-nullable idsalle field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(salle);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Salle> findSalleEntities() {
        return findSalleEntities(true, -1, -1);
    }

    public List<Salle> findSalleEntities(int maxResults, int firstResult) {
        return findSalleEntities(false, maxResults, firstResult);
    }

    private List<Salle> findSalleEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Salle.class));
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

    public Salle findSalle(BigDecimal id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Salle.class, id);
        } finally {
            em.close();
        }
    }

    public int getSalleCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Salle> rt = cq.from(Salle.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
    
    
}
