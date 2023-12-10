/**
 * 
 */
package acmecollege;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import acmecollege.entity.ClubMembership;
import acmecollege.entity.Course;
import acmecollege.entity.CourseRegistration;
import acmecollege.entity.MembershipCard;
import acmecollege.entity.Professor;
import acmecollege.entity.Professor_;
import acmecollege.entity.Student;
import acmecollege.entity.StudentClub;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.List;
import java.util.Objects;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
//import common.JUnitBase;

/**
 * @author Mengya Shi
 *
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
public class TestProfessor {

    private static EntityManagerFactory emf;
    private EntityManager em;
    private EntityTransaction et;
    private static Professor professor;

    // Use @BeforeAll to initialize the EntityManagerFactory
    @BeforeAll
    static void setupAll() {
        emf = Persistence.createEntityManagerFactory("acmecollege-PU");
        deleteAllData();
        professor = new Professor();
        professor.setFirstName("Teddy Yap");
    }

    // Use @BeforeEach to set up EntityManager and EntityTransaction
    @BeforeEach
    void setup() {
        em = emf.createEntityManager();
        et = em.getTransaction();
    }

    // Use @AfterEach to close EntityManager
    @AfterEach
    void tearDown() {
        em.close();
    }

    // Use @AfterAll to clean up resources (close EntityManagerFactory)
    @AfterAll
    static void tearDownAll() {
        deleteAllData();
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }

    @Test
	void test01_Empty() {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Long> query = builder.createQuery(Long.class);
		Root<Professor> root = query.from(Professor.class);
		query.select(builder.count(root));
		TypedQuery<Long> tq = em.createQuery(query);
		long result = tq.getSingleResult();
		assertThat(result, is(comparesEqualTo(0L)));
	}
	
	@Test
	void test02_Create() {
		et.begin();
		professor = new Professor();
		professor.setFirstName("Mengya");
		professor.setLastName("Shi");
		professor.setDepartment("Tech");
		em.persist(professor);
		et.commit();
		
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Long> query = builder.createQuery(Long.class);
		Root<Professor> root = query.from(Professor.class);
		query.select(builder.count(root));
		query.where(builder.equal(root.get(Professor_.id), builder.parameter(Integer.class, "id"))); 
		TypedQuery<Long> tq = em.createQuery(query);
		tq.setParameter("id", professor.getId());
		long result = tq.getSingleResult();
		assertThat(result, is(greaterThanOrEqualTo(1L))); 
	}
	
	@Test
	void test03_CreateInvalid() {
		et.begin();
		Professor professor2 = new Professor();
		assertThrows(PersistenceException.class, () -> em.persist(professor2));
		et.commit();
	}
	
	@Test
	void test04_Read() {
	    CriteriaBuilder builder = em.getCriteriaBuilder();
	    CriteriaQuery<Professor> query = builder.createQuery(Professor.class);
	    Root<Professor> root = query.from(Professor.class);
	    query.select(root);
	    TypedQuery<Professor> tq = em.createQuery(query);
	    List<Professor> professors = tq.getResultList();
	    assertThat(professors, contains(equalTo(professor)));
	}

	@Test
	void test05_Update() {
	    CriteriaBuilder builder = em.getCriteriaBuilder();
	    CriteriaQuery<Professor> query = builder.createQuery(Professor.class);
	    Root<Professor> root = query.from(Professor.class);
	    query.select(root);
	    query.where(builder.equal(root.get(Professor_.id), builder.parameter(Integer.class, "id")));
	    TypedQuery<Professor> tq = em.createQuery(query);
	    tq.setParameter("id", professor.getId());
	    Professor returnedProfessor = tq.getSingleResult();

	    et.begin();
	    returnedProfessor.setFirstName("TeddyXX");
	    returnedProfessor.setLastName("YapXX");
	    em.merge(returnedProfessor);
	    et.commit();

	    returnedProfessor = tq.getSingleResult();

	    assertThat(returnedProfessor.getFirstName(), equalTo("TeddyXX"));
	    assertThat(returnedProfessor.getLastName(), equalTo("YapXX"));
	}
	
	@Test
	void test06_Delete() {
	    CriteriaBuilder builder = em.getCriteriaBuilder();
	    CriteriaQuery<Professor> query = builder.createQuery(Professor.class);
	    Root<Professor> root = query.from(Professor.class);
	    query.select(root);
	    query.where(builder.equal(root.get(Professor_.id), builder.parameter(Integer.class, "id")));
	    TypedQuery<Professor> tq = em.createQuery(query);
	    tq.setParameter("id", professor.getId());
	    Professor returnedProfessor = tq.getSingleResult();
	    et.begin();
	    em.remove(returnedProfessor);
	    et.commit();
	    Exception exception = assertThrows(javax.persistence.NoResultException.class, () -> {
	        tq.getSingleResult();
	    });
	    assertThat(exception.getClass(), equalTo(javax.persistence.NoResultException.class));
	}

    private static void deleteAllData() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = em.getTransaction();
            transaction.begin();
	        deleteAllFrom(MembershipCard.class, em);
	        deleteAllFrom(ClubMembership.class, em);
	        deleteAllFrom(CourseRegistration.class, em);
	        deleteAllFrom(Course.class, em);
	        deleteAllFrom(Student.class, em);
	        deleteAllFrom(Professor.class, em);
			deleteAllFrom(StudentClub.class, em);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
	public static <T> int deleteAllFrom(Class<T> entityType, EntityManager em) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
	    CriteriaDelete<T> delete = cb.createCriteriaDelete(entityType);
	    delete.from(entityType);
	    return em.createQuery(delete).executeUpdate();
	}

}