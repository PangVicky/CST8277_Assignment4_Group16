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
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.soteria.WrappingCallerPrincipal;

import acmecollege.ejb.ACMECollegeService;
import acmecollege.entity.Course;
import acmecollege.entity.CourseRegistration;
import acmecollege.entity.Professor;
import acmecollege.entity.SecurityUser;
import acmecollege.entity.Student;

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
	@Path("/course/{courseId}/student/{studentId}")
	public Response getCourseRegistrationById(@PathParam("courseId") int courseId, @PathParam("studentId") int studentId) {
		Response response = null;	
		CourseRegistration cr = null;
		LOG.debug("try to retrieve a course registration with course id = {}, student id = {}", courseId, studentId);
		if (sc.isCallerInRole(USER_ROLE)) {
			WrappingCallerPrincipal wCallerPrincipal = (WrappingCallerPrincipal) sc.getCallerPrincipal();
            SecurityUser sUser = (SecurityUser) wCallerPrincipal.getWrapped();
            Student student = sUser.getStudent();
            if (student != null && student.getId() == studentId) {
            	cr = service.getCourseRegistrationById(courseId, studentId);
    			response = Response.status(cr == null ? Status.NOT_FOUND : Status.OK).entity(cr).build();
    			return response;
            } else{
                response = Response.status(Status.FORBIDDEN).build();
                return response;
            }
		} 

		cr = service.getCourseRegistrationById(courseId, studentId);
		return Response.ok(cr).build();
	}
	
	@POST
	@RolesAllowed({ADMIN_ROLE})
	@Path("/course/{courseId}/student/{studentId}")
	public Response createNewCourseRegistration(@PathParam("courseId") int courseId, @PathParam("studentId") int studentId, CourseRegistration newCr) {
		LOG.debug("try to create a new course registration with course id = {}, student id = {}", courseId, studentId);
		CourseRegistration cr = service.createCourseRegistration(courseId, studentId, newCr);
		return Response.ok(cr).build();
	}
	
	@PUT
	@RolesAllowed({ADMIN_ROLE})
	@Path("/course/{courseId}/student/{studentId}")
	public Response updateCourseRegistration(@PathParam("courseId") int courseId, @PathParam("studentId") int studentId, CourseRegistration newCr) {
		LOG.debug("try to update course registration with course id = {}, student id = {}", courseId, studentId);
		CourseRegistration cr = service.updateCourseRegistration(courseId, studentId ,newCr);
		return Response.ok(cr).build();
	}
	
//	@PUT
//	@RolesAllowed({ADMIN_ROLE})
//	@Path("/course/{courseId}/student/{studentId}/student")
//	public Response updateStudentToCourseRegistration(@PathParam("courseId") int courseId, @PathParam("studentId") int studentId, Student student) {
//		LOG.debug("try to update student = {} to course registration with course id = {}, student id = {}", student.getId(), courseId, studentId);
//		CourseRegistration cr = service.updateStudentToCourseRegistration(student, courseId, studentId);
//		return Response.ok(cr).build();
//	}
	
//	@PUT
//	@RolesAllowed({ADMIN_ROLE})
//	@Path("/course/{courseId}/student/{studentId}/course")
//	public Response updateCourseToCourseRegistration(@PathParam("studentId") int studentId, @PathParam("courseId") int courseId, Course newCourse) {
//		LOG.debug("try to update course = {} to course registration with course id = {}, student id = {}", newCourse.getId(), courseId, studentId);
//		CourseRegistration cr = service.updateCourseToCourseRegistration(newCourse, courseId, studentId);
//		return Response.ok(cr).build();
//	}
	
	@PUT
	@RolesAllowed({ADMIN_ROLE})
	@Path("/course/{courseId}/student/{studentId}/professor")
	public Response updateProfessorToCourseRegistration(@PathParam("studentId") int studentId, @PathParam("courseId") int courseId, Professor professor) {
		LOG.debug("try to update professor = {} to course registration with course id = {}, student id = {}", professor.getId(), courseId, studentId);
		CourseRegistration cr = service.updateProfessorToCourseRegistration(professor, courseId, studentId);
		return Response.ok(cr).build();
	}
	
	@DELETE
	@RolesAllowed({ADMIN_ROLE})
	@Path("/course/{courseId}/student/{studentId}/professor")
	public Response deleteProfessorToCourseRegistration(@PathParam("studentId") int studentId, @PathParam("courseId") int courseId) {
		LOG.debug("try to delete professor to course registration with course id = {}, student id = {}", courseId, studentId);
		CourseRegistration cr = service.deleteProfessorToCourseRegistration(courseId, studentId);
		return Response.ok(cr).build();
	}
	
	@DELETE
	@RolesAllowed({ADMIN_ROLE})
	@Path("/course/{courseId}/student/{studentId}")
	public Response deleteCourseRegistration(@PathParam("studentId") int studentId, @PathParam("courseId") int courseId) {
		LOG.debug("try to delete course registration with course id = {}, student id = {}", courseId, studentId);
		CourseRegistration cr = service.deleteCourseRegistrationById(courseId, studentId);
		return Response.ok(cr).build();
	}
}
