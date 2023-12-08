package acmecollege.rest.resource;

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
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import acmecollege.ejb.ACMECollegeService;
import acmecollege.entity.Course;

import static acmecollege.utility.MyConstants.COURSE_RESOURCE_NAME;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_ID_ELEMENT;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_ID_PATH;

import java.util.List;

import static acmecollege.utility.MyConstants.ADMIN_ROLE;
import static acmecollege.utility.MyConstants.USER_ROLE;

@Path(COURSE_RESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CourseResource {
	
	private static final Logger LOG = LogManager.getLogger();
	
	@EJB
	protected ACMECollegeService service;
	
	@Inject
	protected SecurityContext sc;
	
	@GET
	@RolesAllowed({ADMIN_ROLE})
	public Response getAllCourses() {
		LOG.debug("retreiving all courses...");
		List<Course> courses = service.getAllCourses();
		Response response = Response.ok(courses).build();
		return response;
	}
	
	@GET
	@RolesAllowed({ADMIN_ROLE, USER_ROLE})
	@Path(RESOURCE_PATH_ID_PATH)
	public Response getCourseById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
		LOG.debug("try to retrieve specific course " + id);
		Course course = service.getCourseById(id);
		Response response = Response.ok(course).build();		
		return response;
	}
	
	@POST
	@RolesAllowed({ADMIN_ROLE})
	public Response createNewCourse(Course newCourse) {
		LOG.debug("try to create a new course");
		if (service.isDuplicated(Course.class, newCourse.getCourseTitle(), Course.IS_DUPLICATE_QUERY_NAME)) {
			HttpErrorResponse err = new HttpErrorResponse(Status.CONFLICT.getStatusCode(), "Entity already exists");
			return Response.status(Status.CONFLICT).entity(err).build();
		} else {
			Course course = service.persistCourse(newCourse);
			return Response.ok(course).build();
		}
	}
	
	@PUT
	@RolesAllowed({ADMIN_ROLE})
	@Path(RESOURCE_PATH_ID_PATH)
	public Response updateCourse(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id, Course courseToUpdate) {
		LOG.debug("try to update a course with id = {}", id);
		Course course = service.updateCourse(id, courseToUpdate);
		return Response.ok(course).build();
	}
	
	@DELETE
	@RolesAllowed({ADMIN_ROLE})
	@Path(RESOURCE_PATH_ID_PATH)
	public Response deleteCourse(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
		LOG.debug("try to delete a course with id = {}", id);
		Course course = service.deleteCourseById(id);
		return Response.ok(course).build();
	}
	
}
