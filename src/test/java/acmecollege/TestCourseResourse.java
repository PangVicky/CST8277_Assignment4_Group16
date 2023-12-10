package acmecollege;

import static acmecollege.utility.MyConstants.COURSE_RESOURCE_NAME;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_ID_PATH;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;

import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import acmecollege.entity.Course;
import acmecollege.entity.CourseRegistration;
import acmecollege.entity.Student;

public class TestCourseResourse extends TestACMECollegeSystem{
	
	@Test
	@Order(1)
	public void test01_all_courses_with_adminrole() {
		Response response = webTarget
	            .register(adminAuth)
	            .path(COURSE_RESOURCE_NAME)
	            .request()
	            .get();
	        assertThat(response.getStatus(), is(200));
	        List<Course> courses = response.readEntity(new GenericType<List<Course>>(){});
	        assertThat(courses, is(not(empty())));
	        assertThat(courses, hasSize(2));
	}
	
	@Test
	@Order(2)
	public void test02_all_courses_with_userrole() {
		Response response = webTarget
	            .register(userAuth)
	            .path(COURSE_RESOURCE_NAME)
	            .request()
	            .get();
		assertThat(response.getStatus(), is(403));
	}
	
	@Test
	@Order(3)
	public void test03_course_by_id_with_adminrole() {
		Response response = webTarget
	            .register(adminAuth)
	            .path(COURSE_RESOURCE_NAME + "/1")
	            .request()
	            .get();
		assertThat(response.getStatus(), is(200));
		Course course = response.readEntity(Course.class);
		assertThat(course.getId(), is(1));
	}
	
	@Test
	@Order(4)
	public void test04_course_by_id_with_userrole() {
		Response response = webTarget
	            .register(userAuth)
	            .path(COURSE_RESOURCE_NAME + "/1")
	            .request()
	            .get();
		assertThat(response.getStatus(), is(200));
		Course course = response.readEntity(Course.class);
		assertThat(course.getId(), is(1));
	}
	
	@Test
	@Order(5)
	public void test05_create_course_with_adminrole() {
		Course course = new Course();
		course.setCourseCode("CST8101");
		course.setCourseTitle("Computer Essentials");
		course.setYear(2022);
		course.setSemester("WINTER");
		course.setCreditUnits(3);
		course.setOnline((byte)0);
		Response response = webTarget
	            .register(adminAuth)
	            .path(COURSE_RESOURCE_NAME)
	            .request()
	            .post(Entity.entity(course, MediaType.APPLICATION_JSON));
		assertThat(response.getStatus(), is(200));
		Course courseRespond = response.readEntity(Course.class);
		assertThat(courseRespond.getCourseCode(), is(course.getCourseCode()));
		assertThat(courseRespond.getCourseTitle(), is(course.getCourseTitle()));
		assertThat(courseRespond.getYear(), is(course.getYear()));
		assertThat(courseRespond.getSemester(), is(course.getSemester()));
		assertThat(courseRespond.getCreditUnits(), is(course.getCreditUnits()));
		assertThat(courseRespond.getOnline(), is(course.getOnline()));
	}
	
	@Test
	@Order(6)
	public void test06_create_course_with_userrole() {
		Course course = new Course();
		course.setCourseCode("CST8101");
		course.setCourseTitle("Computer Essentials");
		course.setYear(2022);
		course.setSemester("WINTER");
		course.setCreditUnits(3);
		course.setOnline((byte)0);
		Response response = webTarget
	            .register(userAuth)
	            .path(COURSE_RESOURCE_NAME)
	            .request()
	            .post(Entity.entity(course, MediaType.APPLICATION_JSON));
		assertThat(response.getStatus(), is(403));
	}
	
	@Test
	@Order(7)
	public void test07_create_course_registration_to_course_with_adminrole() {
		CourseRegistration cr = new CourseRegistration();
		cr.setLetterGrade("A+");
		cr.setNumericGrade(100);
		Response response = webTarget
	            .register(adminAuth)
	            .path(COURSE_RESOURCE_NAME + "/3/student/1")
	            .request()
	            .post(Entity.entity(cr, MediaType.APPLICATION_JSON_TYPE));
		assertThat(response.getStatus(), is(200));
		Course courseRespond = response.readEntity(Course.class);
		cr.getId().setCourseId(3);
		cr.getId().setStudentId(1);
		assertThat(courseRespond.getCourseRegistrations().iterator().next().equals(cr), is(true));
		webTarget
        .register(adminAuth)
        .path(COURSE_RESOURCE_NAME + "/3/student/1")
        .request()
        .delete();
	}
	
	@Test
	@Order(8)
	public void test08_create_course_registration_to_course_with_userrole() {
		CourseRegistration cr = new CourseRegistration();
		cr.setLetterGrade("A+");
		cr.setNumericGrade(100);
		Response response = webTarget
	            .register(userAuth)
	            .path(COURSE_RESOURCE_NAME + "/3/student/1")
	            .request()
	            .post(Entity.entity(cr, MediaType.APPLICATION_JSON));
		assertThat(response.getStatus(), is(403));
	}
	
	@Test
	@Order(9)
	public void test09_update_course_with_adminrole() {
		Course course = new Course();
		course.setCourseCode("CST8101");
		course.setCourseTitle("Computer Essentials");
		course.setYear(2024);
		course.setSemester("SUMMER");
		course.setCreditUnits(3);
		course.setOnline((byte)0);
		Response response = webTarget
	            .register(adminAuth)
	            .path(COURSE_RESOURCE_NAME + "/3")
	            .request()
	            .put(Entity.entity(course, MediaType.APPLICATION_JSON));
		assertThat(response.getStatus(), is(200));
		Course courseRespond = response.readEntity(Course.class);
		course.setId(3);
		assertThat(courseRespond.equals(course), is(true));
	}
	
	@Test
	@Order(10)
	public void test10_update_course_with_userrole() {
		Course course = new Course();
		course.setCourseCode("CST8101");
		course.setCourseTitle("Computer Essentials");
		course.setYear(2024);
		course.setSemester("SUMMER");
		course.setCreditUnits(3);
		course.setOnline((byte)0);
		Response response = webTarget
	            .register(userAuth)
	            .path(COURSE_RESOURCE_NAME + "/3")
	            .request()
	            .put(Entity.entity(course, MediaType.APPLICATION_JSON_TYPE));
		assertThat(response.getStatus(), is(403));
	}
	
	@Test
	@Order(11)
	public void test11_delete_course_with_adminrole() {
		Response response = webTarget
	            .register(adminAuth)
	            .path(COURSE_RESOURCE_NAME + "/3")
	            .request()
	            .delete();
		assertThat(response.getStatus(), is(200));
	}
	
	@Test
	@Order(12)
	public void test12_delete_course_with_userrole() {
		Response response = webTarget
	            .register(userAuth)
	            .path(COURSE_RESOURCE_NAME + "/3")
	            .request()
	            .delete();
		assertThat(response.getStatus(), is(403));
	}

}
