/**
 * File:  ACMEColegeService.java
 * Course materials (23S) CST 8277
 *
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * @author (original) Mike Norman
 * 
 * Updated by:  Group NN
 *   studentId, firstName, lastName (as from ACSIS)
 *   studentId, firstName, lastName (as from ACSIS)
 *   studentId, firstName, lastName (as from ACSIS)
 *   studentId, firstName, lastName (as from ACSIS)
 *
 */
package acmecollege.ejb;

import static acmecollege.entity.StudentClub.SPECIFIC_STUDENT_CLUB_QUERY_NAME;
import static acmecollege.entity.StudentClub.IS_DUPLICATE_QUERY_NAME;
//import static acmecollege.entity.Course.IS_COURSE_DUPLICATE_QUERY_NAME;
import static acmecollege.entity.Student.ALL_STUDENTS_QUERY_NAME;
import static acmecollege.utility.MyConstants.DEFAULT_KEY_SIZE;
import static acmecollege.utility.MyConstants.DEFAULT_PROPERTY_ALGORITHM;
import static acmecollege.utility.MyConstants.DEFAULT_PROPERTY_ITERATIONS;
import static acmecollege.utility.MyConstants.DEFAULT_SALT_SIZE;
import static acmecollege.utility.MyConstants.DEFAULT_USER_PASSWORD;
import static acmecollege.utility.MyConstants.DEFAULT_USER_PREFIX;
import static acmecollege.utility.MyConstants.PARAM1;
import static acmecollege.utility.MyConstants.PROPERTY_ALGORITHM;
import static acmecollege.utility.MyConstants.PROPERTY_ITERATIONS;
import static acmecollege.utility.MyConstants.PROPERTY_KEY_SIZE;
import static acmecollege.utility.MyConstants.PROPERTY_SALT_SIZE;
import static acmecollege.utility.MyConstants.PU_NAME;
import static acmecollege.utility.MyConstants.USER_ROLE;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.security.enterprise.identitystore.Pbkdf2PasswordHash;
import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mysql.cj.x.protobuf.MysqlxDatatypes.Array;

import acmecollege.entity.ClubMembership;
import acmecollege.entity.Course;
import acmecollege.entity.CourseRegistration;
import acmecollege.entity.MembershipCard;
import acmecollege.entity.Professor;
import acmecollege.entity.SecurityRole;
import acmecollege.entity.SecurityUser;
import acmecollege.entity.Student;
import acmecollege.entity.StudentClub;
import acmecollege.entity.CourseRegistrationPK;

@SuppressWarnings("unused")

/**
 * Stateless Singleton EJB Bean - ACMECollegeService
 */
@Singleton
public class ACMECollegeService implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private static final Logger LOG = LogManager.getLogger();
    
    @PersistenceContext(name = PU_NAME)
    protected EntityManager em;
    
    @Inject
    protected Pbkdf2PasswordHash pbAndjPasswordHash;

    /**
     * To get all students
     * @return
     */
    public List<Student> getAllStudents() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Student> cq = cb.createQuery(Student.class);
        cq.select(cq.from(Student.class));
        return em.createQuery(cq).getResultList();
    }

    /**
     * To get a student by id
     * @param id
     * @return
     */
    public Student getStudentById(int id) {
        return em.find(Student.class, id);
    }

    /**
     * To create a new student
     * @param newStudent
     * @return
     */
    @Transactional
    public Student persistStudent(Student newStudent) {
        em.persist(newStudent);
        em.flush();
        return newStudent;
    }
    
    /**
     * To update a student
     * 
     * @param id - id of entity to update
     * @param studentWithUpdates - entity with updated information
     * @return Entity with updated information
     */
    @Transactional
    public Student updateStudentById(int id, Student studentWithUpdates) {
        Student studentToBeUpdated = getStudentById(id);
        if (studentToBeUpdated != null) {
            em.refresh(studentToBeUpdated);
            em.merge(studentWithUpdates);
            em.flush();
        }
        return studentToBeUpdated;
    }

    /**
     * To delete a student by id
     * 
     * @param id - student id to delete
     */
    @Transactional
    public void deleteStudentById(int id) {
        Student student = getStudentById(id);
        if (student != null) {
            em.refresh(student);
            TypedQuery<SecurityUser> findUser = em.createNamedQuery(SecurityUser.FIND_STUDENTS_BY_Security_USER, SecurityUser.class).setParameter("param1", student.getId());
                /* TODO ACMECS02 - Use NamedQuery on SecurityRole to find this related Student
                   so that when we remove it, the relationship from SECURITY_USER table
                   is not dangling
                */ 
            SecurityUser sUser = findUser.getSingleResult();
            em.remove(sUser);
            em.remove(student);
        }
    }
    
    /**
     * To create a new security user for student
     * @param newStudent
     */
    @Transactional
    public void buildUserForNewStudent(Student newStudent) {
        SecurityUser userForNewStudent = new SecurityUser();
        userForNewStudent.setUsername(
            DEFAULT_USER_PREFIX + "_" + newStudent.getFirstName() + "." + newStudent.getLastName());
        Map<String, String> pbAndjProperties = new HashMap<>();
        pbAndjProperties.put(PROPERTY_ALGORITHM, DEFAULT_PROPERTY_ALGORITHM);
        pbAndjProperties.put(PROPERTY_ITERATIONS, DEFAULT_PROPERTY_ITERATIONS);
        pbAndjProperties.put(PROPERTY_SALT_SIZE, DEFAULT_SALT_SIZE);
        pbAndjProperties.put(PROPERTY_KEY_SIZE, DEFAULT_KEY_SIZE);
        pbAndjPasswordHash.initialize(pbAndjProperties);
        String pwHash = pbAndjPasswordHash.generate(DEFAULT_USER_PASSWORD.toCharArray());
        userForNewStudent.setPwHash(pwHash);
        userForNewStudent.setStudent(newStudent);
        /* TODO ACMECS01 - Use NamedQuery on SecurityRole to find USER_ROLE */
        SecurityRole userRole =  em.createNamedQuery(SecurityRole.FIND_USER_ROLE_BY_NAME, SecurityRole.class).setParameter("param1", "USER_ROLE").getSingleResult();
        userForNewStudent.getRoles().add(userRole);
        userRole.getUsers().add(userForNewStudent);
        em.persist(userForNewStudent);
        em.flush();
    }

    /**
     * To set professor for a course registration
     * @param studentId
     * @param courseId
     * @param newProfessor
     * @return
     */
    @Transactional
    public Professor setProfessorForStudentCourse(int studentId, int courseId, Professor newProfessor) {
        Student studentToBeUpdated = em.find(Student.class, studentId);
        if (studentToBeUpdated != null) { // Student exists
            Set<CourseRegistration> courseRegistrations = studentToBeUpdated.getCourseRegistrations();
            courseRegistrations.forEach(c -> {
                if (c.getCourse().getId() == courseId) {
                    if (c.getProfessor() != null) { // Professor exists
                        Professor prof = em.find(Professor.class, c.getProfessor().getId());
                        prof.setProfessor(newProfessor.getFirstName(),
                        				  newProfessor.getLastName(),
                        				  newProfessor.getDepartment());
                        em.merge(prof);
                    }
                    else { // Professor does not exist
                        c.setProfessor(newProfessor);
                        em.merge(studentToBeUpdated);
                    }
                }
            });
            return newProfessor;
        }
        else return null;  // Student doesn't exists
    }
   
    /**
     * To get all student clubs
     * @return
     */
    public List<StudentClub> getAllStudentClubs() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<StudentClub> cq = cb.createQuery(StudentClub.class);
        cq.select(cq.from(StudentClub.class));
        return em.createQuery(cq).getResultList();
    }

    // Why not use the build-in em.find?  The named query SPECIFIC_STUDENT_CLUB_QUERY_NAME
    // includes JOIN FETCH that we cannot add to the above API
    /**
     * To get student club by id
     * @param id
     * @return
     */
    public StudentClub getStudentClubById(int id) {
        TypedQuery<StudentClub> specificStudentClubQuery = em.createNamedQuery(SPECIFIC_STUDENT_CLUB_QUERY_NAME, StudentClub.class);
        specificStudentClubQuery.setParameter(PARAM1, id);
        return specificStudentClubQuery.getSingleResult();
    }
   
    /**
     * To create a new student club
     * @param newStudentClub
     * @return
     */
    @Transactional
    public StudentClub persistStudentClub(StudentClub newStudentClub) {
        em.persist(newStudentClub);
        return newStudentClub;
    }

    /**
     * To update a student club
     * @param id
     * @param updatingStudentClub
     * @return
     */
    @Transactional
    public StudentClub updateStudentClub(int id, StudentClub updatingStudentClub) {
    	StudentClub studentClubToBeUpdated = getStudentClubById(id);
        if (studentClubToBeUpdated != null) {
            em.refresh(studentClubToBeUpdated);
            studentClubToBeUpdated.setName(updatingStudentClub.getName());
            em.merge(studentClubToBeUpdated);
            em.flush();
        }
        return studentClubToBeUpdated;
    }
    
    /**
     * To delete a student club
     * @param id
     * @return
     */
    @Transactional
    public StudentClub deleteStudentClub(int id) {
        //StudentClub sc = getStudentClubById(id);
    	StudentClub sc = getById(StudentClub.class, StudentClub.SPECIFIC_STUDENT_CLUB_QUERY_NAME, id);
        if (sc != null) {
            Set<ClubMembership> memberships = sc.getClubMemberships();
            List<ClubMembership> list = new LinkedList<>();
            memberships.forEach(list::add);
            list.forEach(m -> {
                if (m.getCard() != null) {
                    MembershipCard mc = getById(MembershipCard.class, MembershipCard.ID_CARD_QUERY_NAME, m.getCard().getId());
                    mc.setClubMembership(null);
                }
                m.setCard(null);
                em.merge(m);
            });
            em.remove(sc);
            return sc;
        }
        return null;
    }
    
    /**
     * To get a club membership by id
     * @param cmId
     * @return
     */
    public ClubMembership getClubMembershipById(int cmId) {
        TypedQuery<ClubMembership> allClubMembershipQuery = em.createNamedQuery(ClubMembership.FIND_BY_ID, ClubMembership.class);
        allClubMembershipQuery.setParameter(PARAM1, cmId);
        return allClubMembershipQuery.getSingleResult();
    }
    
    /**
     * To create a new club membership
     * @param newClubMembership
     * @return
     */
    @Transactional
    public ClubMembership persistClubMembership(ClubMembership newClubMembership) {
        em.persist(newClubMembership);
        return newClubMembership;
    }

    /**
     * To update a club membership
     * @param id
     * @param clubMembershipWithUpdates
     * @return
     */
    @Transactional
    public ClubMembership updateClubMembership(int id, ClubMembership clubMembershipWithUpdates) {
    	ClubMembership clubMembershipToBeUpdated = getClubMembershipById(id);
        if (clubMembershipToBeUpdated != null) {
            em.refresh(clubMembershipToBeUpdated);
            em.merge(clubMembershipWithUpdates);
            em.flush();
        }
        return clubMembershipToBeUpdated;
    }
    
    /*
     * To get all courses
     */
    public List<Course> getAllCourses() {
    	return getAll(Course.class, Course.ALL_COURSES_QUERY);
    }
    
    /**
     * To get a course by id
     * @param id
     * @return
     */
    public Course getCourseById(int id) {
    	return  em.find(Course.class, id);	
    }
    /**
     * To create a new course
     * @param newCourse
     * @return
     */
    @Transactional
    public Course persistCourse(Course newCourse) {
    	LOG.debug("try to create a new course");
    	em.persist(newCourse);
    	em.flush();
    	return newCourse;
    }
    /**
     * To update a course
     * @param course
     * @return
     */
    @Transactional
    public Course updateCourse(int id, Course course) {
    	Course courseToUpdate = getCourseById(id);
    	if (courseToUpdate != null) {
    		em.refresh(courseToUpdate);
    		courseToUpdate.setCourseCode(course.getCourseCode());
    		courseToUpdate.setCourseTitle(course.getCourseTitle());
    		courseToUpdate.setYear(course.getYear());
    		courseToUpdate.setCreditUnits(course.getCreditUnits());
    		courseToUpdate.setOnline(course.getOnline());
    		courseToUpdate = em.merge(courseToUpdate);
    		em.flush();
    	}
    	return courseToUpdate;
    }
    
    /**
     * To update course registration to a course
     * @param courseId
     * @param cr
     * @return
     */
    @Transactional
    public Course createCourseRegistrationToCourse(int courseId, int studentId, CourseRegistration cr) {
    	Course courseToUpdate = getCourseById(courseId);
    	if (courseToUpdate != null) {
    		Student student = getStudentById(studentId);
    		cr.setStudent(student);
    		cr.setCourse(courseToUpdate);
    		em.refresh(courseToUpdate);
    		Set<CourseRegistration> crs = courseToUpdate.getCourseRegistrations();
    		crs.add(cr);
    		courseToUpdate.setCourseRegistrations(crs);
    		em.merge(courseToUpdate);
    		em.flush();
    	}
    	return courseToUpdate;
    }
    
    /**
     * To delete a course by id
     * @param id
     * @return
     */
    @Transactional
    public Course deleteCourseById(int id) {
    	Course courseToDelete = getCourseById(id);
    	if (courseToDelete != null) {
    		em.refresh(courseToDelete);
    		em.remove(courseToDelete);
    		em.flush();
    	}
    	return courseToDelete;
    }
    
    /**
     * To get all course registration
     * @return
     */
    public List<CourseRegistration> getAllCourseRegistration() {
    	return getAll(CourseRegistration.class, "CourseRegistration.findAll");
    }
    
    /*
     * To  get course registration by id
     * @param id
     * @return
     */
    public CourseRegistration getCourseRegistrationById(int courseId, int studentId) {
    	CourseRegistrationPK pk = new CourseRegistrationPK();
    	pk.setCourseId(courseId);
    	pk.setStudentId(studentId);
    	return em.find(CourseRegistration.class, pk);
    }
    
    /**
     * To create a new course registration
     * @param cr
     * @return
     */
    @Transactional
    public CourseRegistration createCourseRegistration(int courseId, int studentId, CourseRegistration cr) {
    	Course course = getCourseById(courseId);
    	Student student = getStudentById(studentId);
    	if (course != null && student != null) {
    		cr.getId().setCourseId(courseId);
        	cr.getId().setStudentId(studentId);
        	cr.setCourse(course);
        	cr.setStudent(student);
        	em.persist(cr);
        	em.flush();
    	}
    	
    	return cr;
    }
    
    /**
     * To update a course registration
     * @param id
     * @param cr
     * @return
     */
    @Transactional
    public CourseRegistration updateCourseRegistration(int courseId, int studentId, CourseRegistration cr) {
    	CourseRegistration crToUpdate = getCourseRegistrationById(courseId, studentId);
    	if (crToUpdate != null) {
    		em.refresh(crToUpdate);
    		crToUpdate.setNumericGrade(cr.getNumericGrade());
    		crToUpdate.setLetterGrade(cr.getLetterGrade());
    		em.merge(crToUpdate);
    		em.flush();
    	}
    	return crToUpdate;
    }
    /**
     * To update student to course registration by id
     * @param studentId
     * @param crId
     * @return
     */
    @Transactional
    public CourseRegistration updateStudentToCourseRegistration(Student newStudent, int crId, int studentId ) {
    	Student student = getStudentById(newStudent.getId());
    	CourseRegistration cr = getCourseRegistrationById(crId, studentId);
    	em.refresh(cr);
    	cr.setStudent(student);
    	em.merge(cr);
    	em.flush();
    	return cr;
    }
    
    /**
     * To update course to course registration by id
     * @param courseId
     * @param courseId
     * @return
     */
    @Transactional
    public CourseRegistration updateCourseToCourseRegistration(Course newCourse, int courseId, int studentId) {
    	Course course = getCourseById(newCourse.getId());
    	CourseRegistration cr = getCourseRegistrationById(courseId, studentId);
    	em.refresh(cr);
    	cr.setCourse(course);
    	em.merge(cr);
    	em.flush();
    	return cr;
    }
    
    /**
     * To update professor to course registration
     * @param professorId
     * @param crId
     * @return
     */
    @Transactional
    public CourseRegistration updateProfessorToCourseRegistration(Professor newProfessor, int crId, int studentId) {
    	Professor professor = getProfessorById(newProfessor.getId());
    	CourseRegistration cr = getCourseRegistrationById(crId, studentId);
    	if (professor != null && cr != null) {
    		em.refresh(cr);
        	cr.setProfessor(professor);
        	cr = em.merge(cr);
        	em.flush();
    	}
    	System.out.println("Professor = " + cr.getProfessor().getFirstName());
    	return cr;
    }
    
    /**
     * To delete professor to course registration
     * @param id
     * @return
     */
    @Transactional
    public CourseRegistration deleteProfessorToCourseRegistration(int courseId, int studentId) {
    	CourseRegistration cr = getCourseRegistrationById(courseId, studentId);
    	em.refresh(cr);
    	cr.setProfessor(null);
    	em.merge(cr);
    	em.flush();
    	return cr;
    }
    
    /**
     * To delete a course registration
     * @param id
     * @return
     */
    @Transactional
    public CourseRegistration deleteCourseRegistrationById(int courseId, int studentId) {
    	CourseRegistration cr = getCourseRegistrationById(courseId, studentId);
    	em.refresh(cr);
    	em.remove(cr);
    	em.flush();
    	return cr;
    }
    
    /**
     * To get all professors
     * @return
     */
    public List<Professor> getAllProfessor() {
    	return getAll(Professor.class, "Professor.findAll");
    }
    
    /**
     * To get a professor by id
     * @param professorId
     * @return
     */
    public Professor getProfessorById(int professorId) {
		return em.find(Professor.class, professorId);
	}
    
    /**
     * To create a new professor
     * @param professor
     * @return
     */
    @Transactional
    public Professor createProfessor(Professor professor) {
    	em.persist(professor);
    	return professor;
    }

    /**
     * To update a professor
     * @param id
     * @param professor
     * @return
     */
    @Transactional
    public Professor updateProfessor(int id, Professor professor) {
    	Professor professorToUpdate = getProfessorById(id);
    	if (professorToUpdate != null) {
    		em.refresh(professorToUpdate);
    		professorToUpdate.setFirstName(professor.getFirstName());
    		professorToUpdate.setLastName(professor.getFirstName());
    		professorToUpdate.setDepartment(professor.getDepartment());
    		em.merge(professorToUpdate);
    		em.flush();
    	}
    	return professorToUpdate;
    }
    
    /**
     * To delete a professor by id
     * @param id
     * @return
     */
    @Transactional
    public Professor deleteProfessorById(int id) {
    	Professor professor = getProfessorById(id);
    	if (professor != null) {
    		em.refresh(professor);
    		em.remove(professor);
    		em.flush();
    	}
    	return professor;
    }
    
	// These methods are more generic.
    public <T> List<T> getAll(Class<T> entity, String namedQuery) {
        TypedQuery<T> allQuery = em.createNamedQuery(namedQuery, entity);
        return allQuery.getResultList();
    }
    
    public <T> T getById(Class<T> entity, String namedQuery, int id) {
        TypedQuery<T> allQuery = em.createNamedQuery(namedQuery, entity);
        allQuery.setParameter(PARAM1, id);
        return allQuery.getSingleResult();
    }
    
    // Please study & use the methods below in your test suites
    public boolean isDuplicated(StudentClub newStudentClub) {
        TypedQuery<Long> allStudentClubsQuery = em.createNamedQuery(IS_DUPLICATE_QUERY_NAME, Long.class);
        allStudentClubsQuery.setParameter(PARAM1, newStudentClub.getName());
        return (allStudentClubsQuery.getSingleResult() >= 1);
    }
    
    public <T> boolean isDuplicated(Class<T> entity, String param1, String namedQuery) {
    	TypedQuery<Long> q = em.createNamedQuery(namedQuery, Long.class);
    	q.setParameter(PARAM1, param1);
    	return (q.getSingleResult() >= 1);
    }
    
//    public boolean isCourseDuplicate(Course newCourse) {
//    	TypedQuery<Long> courseIsDuplicate = em.createNamedQuery(IS_COURSE_DUPLICATE_QUERY_NAME, Long.class);
//    	courseIsDuplicate.setParameter(PARAM1, newCourse.getCourseTitle());
//    	return (courseIsDuplicate.getSingleResult() >= 1);
//    }
    
}