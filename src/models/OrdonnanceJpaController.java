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
import entities.Hospitaliser;
import entities.Ordonnance;
import java.math.BigDecimal;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import models.exceptions.NonexistentEntityException;

/**
 *
 * @author DELL
 */
public class OrdonnanceJpaController implements Serializable {

    public OrdonnanceJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Ordonnance ordonnance) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Hospitaliser hospitaliser = ordonnance.getHospitaliser();
            if (hospitaliser != null) {
                hospitaliser = em.getReference(hospitaliser.getClass(), hospitaliser.getHospitaliserPK());
                ordonnance.setHospitaliser(hospitaliser);
            }
            em.persist(ordonnance);
            if (hospitaliser != null) {
                hospitaliser.getOrdonnanceList().add(ordonnance);
                hospitaliser = em.merge(hospitaliser);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Ordonnance ordonnance) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Ordonnance persistentOrdonnance = em.find(Ordonnance.class, ordonnance.getIdordonnance());
            Hospitaliser hospitaliserOld = persistentOrdonnance.getHospitaliser();
            Hospitaliser hospitaliserNew = ordonnance.getHospitaliser();
            if (hospitaliserNew != null) {
                hospitaliserNew = em.getReference(hospitaliserNew.getClass(), hospitaliserNew.getHospitaliserPK());
                ordonnance.setHospitaliser(hospitaliserNew);
            }
            ordonnance = em.merge(ordonnance);
            if (hospitaliserOld != null && !hospitaliserOld.equals(hospitaliserNew)) {
                hospitaliserOld.getOrdonnanceList().remove(ordonnance);
                hospitaliserOld = em.merge(hospitaliserOld);
            }
            if (hospitaliserNew != null && !hospitaliserNew.equals(hospitaliserOld)) {
                hospitaliserNew.getOrdonnanceList().add(ordonnance);
                hospitaliserNew = em.merge(hospitaliserNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                BigDecimal id = ordonnance.getIdordonnance();
                if (findOrdonnance(id) == null) {
                    throw new NonexistentEntityException("The ordonnance with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(BigDecimal id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Ordonnance ordonnance;
            try {
                ordonnance = em.getReference(Ordonnance.class, id);
                ordonnance.getIdordonnance();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The ordonnance with id " + id + " no longer exists.", enfe);
            }
            Hospitaliser hospitaliser = ordonnance.getHospitaliser();
            if (hospitaliser != null) {
                hospitaliser.getOrdonnanceList().remove(ordonnance);
                hospitaliser = em.merge(hospitaliser);
            }
            em.remove(ordonnance);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Ordonnance> findOrdonnanceEntities() {
        return findOrdonnanceEntities(true, -1, -1);
    }

    public List<Ordonnance> findOrdonnanceEntities(int maxResults, int firstResult) {
        return findOrdonnanceEntities(false, maxResults, firstResult);
    }

    private List<Ordonnance> findOrdonnanceEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Ordonnance.class));
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

    public Ordonnance findOrdonnance(BigDecimal id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Ordonnance.class, id);
        } finally {
            em.close();
        }
    }

    public int getOrdonnanceCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Ordonnance> rt = cq.from(Ordonnance.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
