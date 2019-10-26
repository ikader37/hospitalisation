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
import entities.Salle;
import entities.Hospitaliser;
import entities.Lit;
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
public class LitJpaController implements Serializable {

    public LitJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Lit lit) throws PreexistingEntityException, Exception {
        if (lit.getHospitaliserList() == null) {
            lit.setHospitaliserList(new ArrayList<Hospitaliser>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Salle idsalle = lit.getIdsalle();
            if (idsalle != null) {
                idsalle = em.getReference(idsalle.getClass(), idsalle.getIdsalle());
                lit.setIdsalle(idsalle);
            }
            List<Hospitaliser> attachedHospitaliserList = new ArrayList<Hospitaliser>();
            for (Hospitaliser hospitaliserListHospitaliserToAttach : lit.getHospitaliserList()) {
                hospitaliserListHospitaliserToAttach = em.getReference(hospitaliserListHospitaliserToAttach.getClass(), hospitaliserListHospitaliserToAttach.getHospitaliserPK());
                attachedHospitaliserList.add(hospitaliserListHospitaliserToAttach);
            }
            lit.setHospitaliserList(attachedHospitaliserList);
            em.persist(lit);
            if (idsalle != null) {
                idsalle.getLitList().add(lit);
                idsalle = em.merge(idsalle);
            }
            for (Hospitaliser hospitaliserListHospitaliser : lit.getHospitaliserList()) {
                Lit oldLitOfHospitaliserListHospitaliser = hospitaliserListHospitaliser.getLit();
                hospitaliserListHospitaliser.setLit(lit);
                hospitaliserListHospitaliser = em.merge(hospitaliserListHospitaliser);
                if (oldLitOfHospitaliserListHospitaliser != null) {
                    oldLitOfHospitaliserListHospitaliser.getHospitaliserList().remove(hospitaliserListHospitaliser);
                    oldLitOfHospitaliserListHospitaliser = em.merge(oldLitOfHospitaliserListHospitaliser);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findLit(lit.getIdlit()) != null) {
                throw new PreexistingEntityException("Lit " + lit + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Lit lit) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Lit persistentLit = em.find(Lit.class, lit.getIdlit());
            Salle idsalleOld = persistentLit.getIdsalle();
            Salle idsalleNew = lit.getIdsalle();
            List<Hospitaliser> hospitaliserListOld = persistentLit.getHospitaliserList();
            List<Hospitaliser> hospitaliserListNew = lit.getHospitaliserList();
            List<String> illegalOrphanMessages = null;
            for (Hospitaliser hospitaliserListOldHospitaliser : hospitaliserListOld) {
                if (!hospitaliserListNew.contains(hospitaliserListOldHospitaliser)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Hospitaliser " + hospitaliserListOldHospitaliser + " since its lit field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (idsalleNew != null) {
                idsalleNew = em.getReference(idsalleNew.getClass(), idsalleNew.getIdsalle());
                lit.setIdsalle(idsalleNew);
            }
            List<Hospitaliser> attachedHospitaliserListNew = new ArrayList<Hospitaliser>();
            for (Hospitaliser hospitaliserListNewHospitaliserToAttach : hospitaliserListNew) {
                hospitaliserListNewHospitaliserToAttach = em.getReference(hospitaliserListNewHospitaliserToAttach.getClass(), hospitaliserListNewHospitaliserToAttach.getHospitaliserPK());
                attachedHospitaliserListNew.add(hospitaliserListNewHospitaliserToAttach);
            }
            hospitaliserListNew = attachedHospitaliserListNew;
            lit.setHospitaliserList(hospitaliserListNew);
            lit = em.merge(lit);
            if (idsalleOld != null && !idsalleOld.equals(idsalleNew)) {
                idsalleOld.getLitList().remove(lit);
                idsalleOld = em.merge(idsalleOld);
            }
            if (idsalleNew != null && !idsalleNew.equals(idsalleOld)) {
                idsalleNew.getLitList().add(lit);
                idsalleNew = em.merge(idsalleNew);
            }
            for (Hospitaliser hospitaliserListNewHospitaliser : hospitaliserListNew) {
                if (!hospitaliserListOld.contains(hospitaliserListNewHospitaliser)) {
                    Lit oldLitOfHospitaliserListNewHospitaliser = hospitaliserListNewHospitaliser.getLit();
                    hospitaliserListNewHospitaliser.setLit(lit);
                    hospitaliserListNewHospitaliser = em.merge(hospitaliserListNewHospitaliser);
                    if (oldLitOfHospitaliserListNewHospitaliser != null && !oldLitOfHospitaliserListNewHospitaliser.equals(lit)) {
                        oldLitOfHospitaliserListNewHospitaliser.getHospitaliserList().remove(hospitaliserListNewHospitaliser);
                        oldLitOfHospitaliserListNewHospitaliser = em.merge(oldLitOfHospitaliserListNewHospitaliser);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                BigDecimal id = lit.getIdlit();
                if (findLit(id) == null) {
                    throw new NonexistentEntityException("The lit with id " + id + " no longer exists.");
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
            Lit lit;
            try {
                lit = em.getReference(Lit.class, id);
                lit.getIdlit();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The lit with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Hospitaliser> hospitaliserListOrphanCheck = lit.getHospitaliserList();
            for (Hospitaliser hospitaliserListOrphanCheckHospitaliser : hospitaliserListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Lit (" + lit + ") cannot be destroyed since the Hospitaliser " + hospitaliserListOrphanCheckHospitaliser + " in its hospitaliserList field has a non-nullable lit field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Salle idsalle = lit.getIdsalle();
            if (idsalle != null) {
                idsalle.getLitList().remove(lit);
                idsalle = em.merge(idsalle);
            }
            em.remove(lit);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Lit> findLitEntities() {
        return findLitEntities(true, -1, -1);
    }

    public List<Lit> findLitEntities(int maxResults, int firstResult) {
        return findLitEntities(false, maxResults, firstResult);
    }

    private List<Lit> findLitEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Lit.class));
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

    public Lit findLit(BigDecimal id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Lit.class, id);
        } finally {
            em.close();
        }
    }

    public int getLitCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Lit> rt = cq.from(Lit.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
