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
import entities.Etudiant;
import entities.Filiere;
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
public class FiliereJpaController implements Serializable {

    public FiliereJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Filiere filiere) throws PreexistingEntityException, Exception {
        if (filiere.getEtudiantList() == null) {
            filiere.setEtudiantList(new ArrayList<Etudiant>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Etudiant> attachedEtudiantList = new ArrayList<Etudiant>();
            for (Etudiant etudiantListEtudiantToAttach : filiere.getEtudiantList()) {
                etudiantListEtudiantToAttach = em.getReference(etudiantListEtudiantToAttach.getClass(), etudiantListEtudiantToAttach.getIdetudiant());
                attachedEtudiantList.add(etudiantListEtudiantToAttach);
            }
            filiere.setEtudiantList(attachedEtudiantList);
            em.persist(filiere);
            for (Etudiant etudiantListEtudiant : filiere.getEtudiantList()) {
                Filiere oldIdfiliereOfEtudiantListEtudiant = etudiantListEtudiant.getIdfiliere();
                etudiantListEtudiant.setIdfiliere(filiere);
                etudiantListEtudiant = em.merge(etudiantListEtudiant);
                if (oldIdfiliereOfEtudiantListEtudiant != null) {
                    oldIdfiliereOfEtudiantListEtudiant.getEtudiantList().remove(etudiantListEtudiant);
                    oldIdfiliereOfEtudiantListEtudiant = em.merge(oldIdfiliereOfEtudiantListEtudiant);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findFiliere(filiere.getIdfiliere()) != null) {
                throw new PreexistingEntityException("Filiere " + filiere + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Filiere filiere) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Filiere persistentFiliere = em.find(Filiere.class, filiere.getIdfiliere());
            List<Etudiant> etudiantListOld = persistentFiliere.getEtudiantList();
            List<Etudiant> etudiantListNew = filiere.getEtudiantList();
            List<String> illegalOrphanMessages = null;
            for (Etudiant etudiantListOldEtudiant : etudiantListOld) {
                if (!etudiantListNew.contains(etudiantListOldEtudiant)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Etudiant " + etudiantListOldEtudiant + " since its idfiliere field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Etudiant> attachedEtudiantListNew = new ArrayList<Etudiant>();
            for (Etudiant etudiantListNewEtudiantToAttach : etudiantListNew) {
                etudiantListNewEtudiantToAttach = em.getReference(etudiantListNewEtudiantToAttach.getClass(), etudiantListNewEtudiantToAttach.getIdetudiant());
                attachedEtudiantListNew.add(etudiantListNewEtudiantToAttach);
            }
            etudiantListNew = attachedEtudiantListNew;
            filiere.setEtudiantList(etudiantListNew);
            filiere = em.merge(filiere);
            for (Etudiant etudiantListNewEtudiant : etudiantListNew) {
                if (!etudiantListOld.contains(etudiantListNewEtudiant)) {
                    Filiere oldIdfiliereOfEtudiantListNewEtudiant = etudiantListNewEtudiant.getIdfiliere();
                    etudiantListNewEtudiant.setIdfiliere(filiere);
                    etudiantListNewEtudiant = em.merge(etudiantListNewEtudiant);
                    if (oldIdfiliereOfEtudiantListNewEtudiant != null && !oldIdfiliereOfEtudiantListNewEtudiant.equals(filiere)) {
                        oldIdfiliereOfEtudiantListNewEtudiant.getEtudiantList().remove(etudiantListNewEtudiant);
                        oldIdfiliereOfEtudiantListNewEtudiant = em.merge(oldIdfiliereOfEtudiantListNewEtudiant);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                BigDecimal id = filiere.getIdfiliere();
                if (findFiliere(id) == null) {
                    throw new NonexistentEntityException("The filiere with id " + id + " no longer exists.");
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
            Filiere filiere;
            try {
                filiere = em.getReference(Filiere.class, id);
                filiere.getIdfiliere();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The filiere with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Etudiant> etudiantListOrphanCheck = filiere.getEtudiantList();
            for (Etudiant etudiantListOrphanCheckEtudiant : etudiantListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Filiere (" + filiere + ") cannot be destroyed since the Etudiant " + etudiantListOrphanCheckEtudiant + " in its etudiantList field has a non-nullable idfiliere field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(filiere);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Filiere> findFiliereEntities() {
        return findFiliereEntities(true, -1, -1);
    }

    public List<Filiere> findFiliereEntities(int maxResults, int firstResult) {
        return findFiliereEntities(false, maxResults, firstResult);
    }

    private List<Filiere> findFiliereEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Filiere.class));
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

    public Filiere findFiliere(BigDecimal id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Filiere.class, id);
        } finally {
            em.close();
        }
    }

    public int getFiliereCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Filiere> rt = cq.from(Filiere.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
