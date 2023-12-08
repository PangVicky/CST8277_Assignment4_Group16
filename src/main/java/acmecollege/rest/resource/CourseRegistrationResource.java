package acmecollege.rest.resource;

import static acmecollege.utility.MyConstants.COURSE_REGISTRATION_RESOURCE_NAME;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_ID_PATH;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_ID_ELEMENT;
import static acmecollege.utility.MyConstants.COURSE_REGISTRATION_PROFESSOR_RESOURSE_PATH;

import java.util.List;

import static acmecollege.utility.MyConstants.ADMIN_ROLE;
import static acmecollege.utility.MyConstants.USER_ROLE;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.security.enterprise.SecurityContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import acmecollege.ejb.ACMECollegeService;
import acmecollege.entity.CourseRegistration;

@Path(COURSE_REGISTRATION_RESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CourseRegistrationResource {
	
	private static Logger LOG = LogManager.getLogger();
	
	@EJB
	protected ACMECollegeService service;
	
	@Inject
	protected SecurityContext sc;
	
	@GET
	@RolesAllowed({ADMIN_ROLE})
	public Response getAllCourseRegistration() {
		LOG.debug("try to retrieving all course restrations");
		List<CourseRegistration> crs = service.getAllCourseRegistration();
		return Response.ok(crs).build();
	}
	
	@GET
	@RolesAllowed({ADMIN_ROLE, USER_ROLE})
	@Path(RESOURCE_PATH_ID_PATH)
	public Response getCourseRegistrationById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
		LOG.debug("try to retrieve a course registration with id = {}", id);
		CourseRegistration cr = service.getCourseRegistrationById(id);
		return Response.ok(cr).build();
	}
	
	@POST
	@RolesAllowed({ADMIN_ROLE})
	public Response createNewCourseRegistration(CourseRegistration newCr) {
		LOG.debug("try to create a new course registration");
		CourseRegistration cr = service.createCourseRegistration(newCr);
		return Response.ok(cr).build();
	}
	
	@PUT
	@RolesAllowed({ADMIN_ROLE})
	@Path(RESOURCE_PATH_ID_PATH)
	public Response updateCourseRegistration(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id, CourseRegistration newCr) {
		LOG.debug("try to update course registration with id= {}", id);
		CourseRegistration cr = service.updateCourseRegistration(id ,newCr);
		return Response.ok(cr).build();
	}
	
	@PUT
	@RolesAllowed({ADMIN_ROLE})
	@Path("/{courseregistrationid}/student/{studentid}")
	public Response updateStudentToCourseRegistration(@PathParam("courseregistrationid") int crId, @PathParam("studentid") int studentId) {
		LOG.debug("try to update student = {} to course registration = {}", crId, studentId);
		CourseRegistration cr = service.updateStudentToCourseRegistration(studentId, crId);
		return Response.ok(cr).build();
	}
	
	@PUT
	@RolesAllowed({ADMIN_ROLE})
	@Path("/{courseregistrationid}/course/{courseid}")
	public Response updateCourseToCourseRegistration(@PathParam("courseregistrationid") int crId, @PathParam("courseid") int courseId) {
		LOG.debug("try to update course = {} to course registration = {}", crId, courseId);
		CourseRegistration cr = service.updateCourseToCourseRegistration(courseId, crId);
		return Response.ok(cr).build();
	}
	
	@PUT
	@RolesAllowed({ADMIN_ROLE})
	@Path("/{courseregistrationid}/professor/{professorid}")
	public Response updateProfessorToCourseRegistration(@PathParam("courseregistrationid") int crId, @PathParam("professorid") int professorId) {
		LOG.debug("try to update professor = {} to course registration = {}", crId, professorId);
		CourseRegistration cr = service.updateCourseToCourseRegistration(professorId, crId);
		return Response.ok(cr).build();
	}
	
	@DELETE
	@RolesAllowed({ADMIN_ROLE})
	@Path(COURSE_REGISTRATION_PROFESSOR_RESOURSE_PATH) // /{id}/professor
	public Response deleteProfessorToCourseRegistration(@PathParam(RESOURCE_PATH_ID_ELEMENT) int crId) {
		LOG.debug("try to delete professor = {} to course registration = {}", crId);
		CourseRegistration cr = service.deleteProfessorToCourseRegistration(crId);
		return Response.ok(cr).build();
	}
	
	@DELETE
	@RolesAllowed({ADMIN_ROLE})
	@Path(RESOURCE_PATH_ID_PATH)
	public Response deleteCourseRegistration(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
		LOG.debug("try to delete course registration with id= {}", id);
		CourseRegistration cr = service.deleteCourseRegistrationById(id);
		return Response.ok(cr).build();
	}
}
