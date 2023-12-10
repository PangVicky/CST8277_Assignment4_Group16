package acmecollege;

import static acmecollege.utility.MyConstants.COURSE_REGISTRATION_RESOURCE_NAME;
import static acmecollege.utility.MyConstants.STUDENT_RESOURCE_NAME;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import acmecollege.entity.CourseRegistration;
import acmecollege.entity.Professor;
import acmecollege.entity.Student;

public class TestCourseRegistrationResourse extends TestACMECollegeSystem{
	private static Student newStudent = null;
	
	@AfterAll
	public static void cleanUp() {
		webTarget
        .register(adminAuth)
        .path(STUDENT_RESOURCE_NAME + "/" + newStudent.getId())
        .request()
        .delete();
	}
	
	@Test
	@Order(1)
	public void test01_all_course_registration_with_adminrole() {
		Response response = webTarget
	            .register(adminAuth)
	            .path(COURSE_REGISTRATION_RESOURCE_NAME)
	            .request()
	            .get();
	        assertThat(response.getStatus(), is(200));
	        List<CourseRegistration> courses = response.readEntity(new GenericType<List<CourseRegistration>>(){});
	        assertThat(courses, is(not(empty())));
	        assertThat(courses, hasSize(2));
	}
	
	@Test
	@Order(2)
	public void test02_all_course_registration_with_userrole() {
		Response response = webTarget
	            .register(userAuth)
	            .path(COURSE_REGISTRATION_RESOURCE_NAME)
	            .request()
	            .get();
	        assertThat(response.getStatus(), is(403));
	}
	
	@Test
	@Order(3)
	public void test03_course_registration_by_id_with_adminrole() {
		Response response = webTarget
	            .register(adminAuth)
	            .path(COURSE_REGISTRATION_RESOURCE_NAME + "/course/1/student/1")
	            .request()
	            .get();
	    assertThat(response.getStatus(), is(200));
	    CourseRegistration cr = response.readEntity(CourseRegistration.class);
    	assertThat(cr.getId().getCourseId(), is(1)); 
    	assertThat(cr.getId().getStudentId(), is(1));
	     
	}
	
	@Test
	@Order(4)
	public void test04_course_registration_by_id_with_userrole() {
		Response response = webTarget
	            .register(userAuth)
	            .path(COURSE_REGISTRATION_RESOURCE_NAME + "/course/1/student/1")
	            .request()
	            .get();
	    assertThat(response.getStatus(), is(200));
	    CourseRegistration cr = response.readEntity(CourseRegistration.class);
    	assertThat(cr.getId().getCourseId(), is(1)); 
    	assertThat(cr.getId().getStudentId(), is(1));   
	}
	
	@Test
	@Order(5)
	public void create_new_student_for_following_testing() {
		newStudent = new Student();
		newStudent.setFirstName("Jane");
		newStudent.setLastName("Smith");
		
		Response response = webTarget
		        .register(adminAuth)
		        .path(STUDENT_RESOURCE_NAME)
		        .request()
		        .post(Entity.entity(newStudent, MediaType.APPLICATION_JSON));
		assertThat(response.getStatus(), is(200));
		newStudent = response.readEntity(Student.class);
		assertThat(newStudent.getId(), is(newStudent.getId()));
	}
		
		
	@Test
	@Order(6)
	public void test06_course_registration_by_id_with_wrong_userrole() {
//			Student newStudent = new Student();
//			newStudent.setFirstName("Michael");
//			newStudent.setLastName("Smith");
		
//			webTarget
//	        .register(userAuth)
//	        .path(STUDENT_RESOURCE_NAME)
//	        .request()
//	        .post(Entity.entity(newStudent, MediaType.APPLICATION_JSON));
		
		Response response = webTarget
	            .register(userAuth)
	            .path(COURSE_REGISTRATION_RESOURCE_NAME + "/course/1/student/" + newStudent.getId())
	            .request()
	            .get();
	    assertThat(response.getStatus(), is(403));  
	}
		
	
//	@Test
//	@Order(4)
//	public void test04_course_registration_by_id_with_wrong_userrole() {
////		Student newStudent = new Student();
////		newStudent.setFirstName("Michael");
////		newStudent.setLastName("Smith");
//		
////		webTarget
////        .register(userAuth)
////        .path(STUDENT_RESOURCE_NAME)
////        .request()
////        .post(Entity.entity(newStudent, MediaType.APPLICATION_JSON));
//		
//		Response response = webTarget
//	            .register(userAuth)
//	            .path(COURSE_REGISTRATION_RESOURCE_NAME + "/course/1/student/2")
//	            .request()
//	            .get();
//	    assertThat(response.getStatus(), is(403));  
//	}
	
	@Test
	@Order(7)
	public void test07_create_new_course_registration_with_adminrole() {
		CourseRegistration cr = new CourseRegistration();
		cr.setLetterGrade("A+");
		cr.setNumericGrade(100);
		Response response = webTarget
		        .register(adminAuth)
		        .path(COURSE_REGISTRATION_RESOURCE_NAME + "/course/1/student/" + newStudent.getId())
		        .request()
		        .post(Entity.entity(cr, MediaType.APPLICATION_JSON));
		assertThat(response.getStatus(), is(200));
		cr.getId().setCourseId(1);
		cr.getId().setStudentId(newStudent.getId());
		CourseRegistration crRespond = response.readEntity(CourseRegistration.class);
		assertThat(crRespond.equals(cr), is(true));
	}
	
	@Test
	@Order(8)
	public void test08_create_new_course_registration_with_userrole() {
		CourseRegistration cr = new CourseRegistration();
		cr.setLetterGrade("A+");
		cr.setNumericGrade(100);
		Response response = webTarget
		        .register(userAuth)
		        .path(COURSE_REGISTRATION_RESOURCE_NAME + "/course/1/student/" + newStudent.getId())
		        .request()
		        .post(Entity.entity(cr, MediaType.APPLICATION_JSON));
		assertThat(response.getStatus(), is(403));
	}
	
	@Test
	@Order(9)
	public void test09_update_course_registration_with_adminrole() {
		CourseRegistration cr = new CourseRegistration();
		cr.setLetterGrade("A");
		cr.setNumericGrade(90);
		Response response = webTarget
		        .register(adminAuth)
		        .path(COURSE_REGISTRATION_RESOURCE_NAME + "/course/1/student/" + newStudent.getId())
		        .request()
		        .put(Entity.entity(cr, MediaType.APPLICATION_JSON));
		assertThat(response.getStatus(), is(200));
		cr.getId().setCourseId(1);
		cr.getId().setStudentId(newStudent.getId());
		CourseRegistration crRespond = response.readEntity(CourseRegistration.class);
		assertThat(crRespond.equals(cr), is(true));
	}
	
	@Test
	@Order(10)
	public void test10_create_new_course_registration_with_userrole() {
		CourseRegistration cr = new CourseRegistration();
		cr.setLetterGrade("A");
		cr.setNumericGrade(90);
		Response response = webTarget
		        .register(userAuth)
		        .path(COURSE_REGISTRATION_RESOURCE_NAME + "/course/1/student/2")
		        .request()
		        .put(Entity.entity(cr, MediaType.APPLICATION_JSON));
		assertThat(response.getStatus(), is(403));
	}
	
	@Test
	@Order(11)
	public void test11_update_professor_to_course_registration_with_adminrole() {
		Professor professor = new Professor();
		professor.setId(1);
		Response response = webTarget
		        .register(adminAuth)
		        .path(COURSE_REGISTRATION_RESOURCE_NAME + "/course/1/student/" + newStudent.getId() + "/professor")
		        .request()
		        .put(Entity.entity(professor, MediaType.APPLICATION_JSON));
		assertThat(response.getStatus(), is(200));
		CourseRegistration cr = response.readEntity(CourseRegistration.class);
		assertThat(cr.getProfessor().getId(), is(professor.getId()));
	}
	
	@Test
	@Order(12)
	public void test12_update_professor_to_course_registration_with_userrole() {
		Professor professor = new Professor();
		professor.setId(2);
		Response response = webTarget
		        .register(userAuth)
		        .path(COURSE_REGISTRATION_RESOURCE_NAME + "/course/1/student/2/professor")
		        .request()
		        .put(Entity.entity(professor, MediaType.APPLICATION_JSON));
		assertThat(response.getStatus(), is(403));
	}
	
	@Test
	@Order(13)
	public void test13_delete_professor_to_course_registration_wit_adminrole() {
		Response response = webTarget
		        .register(adminAuth)
		        .path(COURSE_REGISTRATION_RESOURCE_NAME + "/course/1/student/"+ newStudent.getId() +"/professor")
		        .request()
		        .delete();
		assertThat(response.getStatus(), is(200));
		CourseRegistration cr = response.readEntity(CourseRegistration.class);
		assertNull(cr.getProfessor());
	}
	
	@Test
	@Order(14)
	public void test14_delete_professor_to_course_registration_with_userrole() {
		Response response = webTarget
		        .register(userAuth)
		        .path(COURSE_REGISTRATION_RESOURCE_NAME + "/course/1/student/2/professor")
		        .request()
		        .delete();
		assertThat(response.getStatus(), is(403));
	}
	
	@Test
	@Order(15)
	public void test15_delete_course_registration_with_adminrole() {
		Response response = webTarget
		        .register(adminAuth)
		        .path(COURSE_REGISTRATION_RESOURCE_NAME + "/course/1/student/" + newStudent.getId())
		        .request()
		        .delete();
		assertThat(response.getStatus(), is(200));
	}
	
	@Test
	@Order(16)
	public void test16_delete_course_registration_with_userrole() {
		Response response = webTarget
		        .register(userAuth)
		        .path(COURSE_REGISTRATION_RESOURCE_NAME + "/course/1/student/2")
		        .request()
		        .delete();
		assertThat(response.getStatus(), is(403));
	}
}
