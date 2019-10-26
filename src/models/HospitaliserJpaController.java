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
import entities.Hospitaliser;
import entities.HospitaliserPK;
import entities.Lit;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import models.exceptions.NonexistentEntityException;
import models.exceptions.PreexistingEntityException;

/**
 *
 * @author DELL
 */
public class HospitaliserJpaController implements Serializable {

    public HospitaliserJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Hospitaliser hospitaliser) throws PreexistingEntityException, Exception {
        if (hospitaliser.getHospitaliserPK() == null) {
            hospitaliser.setHospitaliserPK(new HospitaliserPK());
        }
        hospitaliser.getHospitaliserPK().setIdlit(hospitaliser.getLit().getIdlit().toBigInteger());
        hospitaliser.getHospitaliserPK().setIdetudiant(hospitaliser.getEtudiant().getIdetudiant().toBigInteger());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Etudiant etudiant = hospitaliser.getEtudiant();
            if (etudiant != null) {
                etudiant = em.getReference(etudiant.getClass(), etudiant.getIdetudiant());
                hospitaliser.setEtudiant(etudiant);
            }
            Lit lit = hospitaliser.getLit();
            if (lit != null) {
                lit = em.getReference(lit.getClass(), lit.getIdlit());
                hospitaliser.setLit(lit);
            }
            em.persist(hospitaliser);
            if (etudiant != null) {
                etudiant.getHospitaliserList().add(hospitaliser);
                etudiant = em.merge(etudiant);
            }
            if (lit != null) {
                lit.getHospitaliserList().add(hospitaliser);
                lit = em.merge(lit);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findHospitaliser(hospitaliser.getHospitaliserPK()) != null) {
                throw new PreexistingEntityException("Hospitaliser " + hospitaliser + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Hospitaliser hospitaliser) throws NonexistentEntityException, Exception {
        hospitaliser.getHospitaliserPK().setIdlit(hospitaliser.getLit().getIdlit().toBigInteger());
        hospitaliser.getHospitaliserPK().setIdetudiant(hospitaliser.getEtudiant().getIdetudiant().toBigInteger());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Hospitaliser persistentHospitaliser = em.find(Hospitaliser.class, hospitaliser.getHospitaliserPK());
            Etudiant etudiantOld = persistentHospitaliser.getEtudiant();
            Etudiant etudiantNew = hospitaliser.getEtudiant();
            Lit litOld = persistentHospitaliser.getLit();
            Lit litNew = hospitaliser.getLit();
            if (etudiantNew != null) {
                etudiantNew = em.getReference(etudiantNew.getClass(), etudiantNew.getIdetudiant());
                hospitaliser.setEtudiant(etudiantNew);
            }
            if (litNew != null) {
                litNew = em.getReference(litNew.getClass(), litNew.getIdlit());
                hospitaliser.setLit(litNew);
            }
            hospitaliser = em.merge(hospitaliser);
            if (etudiantOld != null && !etudiantOld.equals(etudiantNew)) {
                etudiantOld.getHospitaliserList().remove(hospitaliser);
                etudiantOld = em.merge(etudiantOld);
            }
            if (etudiantNew != null && !etudiantNew.equals(etudiantOld)) {
                etudiantNew.getHospitaliserList().add(hospitaliser);
                etudiantNew = em.merge(etudiantNew);
            }
            if (litOld != null && !litOld.equals(litNew)) {
                litOld.getHospitaliserList().remove(hospitaliser);
                litOld = em.merge(litOld);
            }
            if (litNew != null && !litNew.equals(litOld)) {
                litNew.getHospitaliserList().add(hospitaliser);
                litNew = em.merge(litNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                HospitaliserPK id = hospitaliser.getHospitaliserPK();
                if (findHospitaliser(id) == null) {
                    throw new NonexistentEntityException("The hospitaliser with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(HospitaliserPK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Hospitaliser hospitaliser;
            try {
                hospitaliser = em.getReference(Hospitaliser.class, id);
                hospitaliser.getHospitaliserPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The hospitaliser with id " + id + " no longer exists.", enfe);
            }
            Etudiant etudiant = hospitaliser.getEtudiant();
            if (etudiant != null) {
                etudiant.getHospitaliserList().remove(hospitaliser);
                etudiant = em.merge(etudiant);
            }
            Lit lit = hospitaliser.getLit();
            if (lit != null) {
                lit.getHospitaliserList().remove(hospitaliser);
                lit = em.merge(lit);
            }
            em.remove(hospitaliser);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Hospitaliser> findHospitaliserEntities() {
        return findHospitaliserEntities(true, -1, -1);
    }

    public List<Hospitaliser> findHospitaliserEntities(int maxResults, int firstResult) {
        return findHospitaliserEntities(false, maxResults, firstResult);
    }

    private List<Hospitaliser> findHospitaliserEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Hospitaliser.class));
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

    public Hospitaliser findHospitaliser(HospitaliserPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Hospitaliser.class, id);
        } finally {
            em.close();
        }
    }

    public int getHospitaliserCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Hospitaliser> rt = cq.from(Hospitaliser.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
