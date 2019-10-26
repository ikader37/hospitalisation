/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import entities.Etudiant;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entities.Filiere;
import entities.Hospitaliser;
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
public class EtudiantJpaController implements Serializable {

    public EtudiantJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Etudiant etudiant) throws PreexistingEntityException, Exception {
        if (etudiant.getHospitaliserList() == null) {
            etudiant.setHospitaliserList(new ArrayList<Hospitaliser>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Filiere idfiliere = etudiant.getIdfiliere();
            if (idfiliere != null) {
                idfiliere = em.getReference(idfiliere.getClass(), idfiliere.getIdfiliere());
                etudiant.setIdfiliere(idfiliere);
            }
            List<Hospitaliser> attachedHospitaliserList = new ArrayList<Hospitaliser>();
            for (Hospitaliser hospitaliserListHospitaliserToAttach : etudiant.getHospitaliserList()) {
                hospitaliserListHospitaliserToAttach = em.getReference(hospitaliserListHospitaliserToAttach.getClass(), hospitaliserListHospitaliserToAttach.getHospitaliserPK());
                attachedHospitaliserList.add(hospitaliserListHospitaliserToAttach);
            }
            etudiant.setHospitaliserList(attachedHospitaliserList);
            em.persist(etudiant);
            if (idfiliere != null) {
                idfiliere.getEtudiantList().add(etudiant);
                idfiliere = em.merge(idfiliere);
            }
            for (Hospitaliser hospitaliserListHospitaliser : etudiant.getHospitaliserList()) {
                Etudiant oldEtudiantOfHospitaliserListHospitaliser = hospitaliserListHospitaliser.getEtudiant();
                hospitaliserListHospitaliser.setEtudiant(etudiant);
                hospitaliserListHospitaliser = em.merge(hospitaliserListHospitaliser);
                if (oldEtudiantOfHospitaliserListHospitaliser != null) {
                    oldEtudiantOfHospitaliserListHospitaliser.getHospitaliserList().remove(hospitaliserListHospitaliser);
                    oldEtudiantOfHospitaliserListHospitaliser = em.merge(oldEtudiantOfHospitaliserListHospitaliser);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            //System.out.println("EXC: "+ex.toString());
            if (findEtudiant(etudiant.getIdetudiant()) != null) {
                
                throw new PreexistingEntityException("Etudiant " + etudiant + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Etudiant etudiant) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Etudiant persistentEtudiant = em.find(Etudiant.class, etudiant.getIdetudiant());
            Filiere idfiliereOld = persistentEtudiant.getIdfiliere();
            Filiere idfiliereNew = etudiant.getIdfiliere();
            List<Hospitaliser> hospitaliserListOld = persistentEtudiant.getHospitaliserList();
            List<Hospitaliser> hospitaliserListNew = etudiant.getHospitaliserList();
            List<String> illegalOrphanMessages = null;
            for (Hospitaliser hospitaliserListOldHospitaliser : hospitaliserListOld) {
                if (!hospitaliserListNew.contains(hospitaliserListOldHospitaliser)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Hospitaliser " + hospitaliserListOldHospitaliser + " since its etudiant field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (idfiliereNew != null) {
                idfiliereNew = em.getReference(idfiliereNew.getClass(), idfiliereNew.getIdfiliere());
                etudiant.setIdfiliere(idfiliereNew);
            }
            List<Hospitaliser> attachedHospitaliserListNew = new ArrayList<Hospitaliser>();
            for (Hospitaliser hospitaliserListNewHospitaliserToAttach : hospitaliserListNew) {
                hospitaliserListNewHospitaliserToAttach = em.getReference(hospitaliserListNewHospitaliserToAttach.getClass(), hospitaliserListNewHospitaliserToAttach.getHospitaliserPK());
                attachedHospitaliserListNew.add(hospitaliserListNewHospitaliserToAttach);
            }
            hospitaliserListNew = attachedHospitaliserListNew;
            etudiant.setHospitaliserList(hospitaliserListNew);
            etudiant = em.merge(etudiant);
            if (idfiliereOld != null && !idfiliereOld.equals(idfiliereNew)) {
                idfiliereOld.getEtudiantList().remove(etudiant);
                idfiliereOld = em.merge(idfiliereOld);
            }
            if (idfiliereNew != null && !idfiliereNew.equals(idfiliereOld)) {
                idfiliereNew.getEtudiantList().add(etudiant);
                idfiliereNew = em.merge(idfiliereNew);
            }
            for (Hospitaliser hospitaliserListNewHospitaliser : hospitaliserListNew) {
                if (!hospitaliserListOld.contains(hospitaliserListNewHospitaliser)) {
                    Etudiant oldEtudiantOfHospitaliserListNewHospitaliser = hospitaliserListNewHospitaliser.getEtudiant();
                    hospitaliserListNewHospitaliser.setEtudiant(etudiant);
                    hospitaliserListNewHospitaliser = em.merge(hospitaliserListNewHospitaliser);
                    if (oldEtudiantOfHospitaliserListNewHospitaliser != null && !oldEtudiantOfHospitaliserListNewHospitaliser.equals(etudiant)) {
                        oldEtudiantOfHospitaliserListNewHospitaliser.getHospitaliserList().remove(hospitaliserListNewHospitaliser);
                        oldEtudiantOfHospitaliserListNewHospitaliser = em.merge(oldEtudiantOfHospitaliserListNewHospitaliser);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                BigDecimal id = etudiant.getIdetudiant();
                if (findEtudiant(id) == null) {
                    throw new NonexistentEntityException("The etudiant with id " + id + " no longer exists.");
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
            Etudiant etudiant;
            try {
                etudiant = em.getReference(Etudiant.class, id);
                etudiant.getIdetudiant();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The etudiant with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Hospitaliser> hospitaliserListOrphanCheck = etudiant.getHospitaliserList();
            for (Hospitaliser hospitaliserListOrphanCheckHospitaliser : hospitaliserListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Etudiant (" + etudiant + ") cannot be destroyed since the Hospitaliser " + hospitaliserListOrphanCheckHospitaliser + " in its hospitaliserList field has a non-nullable etudiant field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Filiere idfiliere = etudiant.getIdfiliere();
            if (idfiliere != null) {
                idfiliere.getEtudiantList().remove(etudiant);
                idfiliere = em.merge(idfiliere);
            }
            em.remove(etudiant);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Etudiant> findEtudiantEntities() {
        return findEtudiantEntities(true, -1, -1);
    }

    public List<Etudiant> findEtudiantEntities(int maxResults, int firstResult) {
        return findEtudiantEntities(false, maxResults, firstResult);
    }

    private List<Etudiant> findEtudiantEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Etudiant.class));
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

    public Etudiant findEtudiant(BigDecimal id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Etudiant.class, id);
        } finally {
            em.close();
        }
    }

    public int getEtudiantCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Etudiant> rt = cq.from(Etudiant.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
