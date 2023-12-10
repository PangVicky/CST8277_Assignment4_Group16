package acmecollege.rest.resource;

import static acmecollege.utility.MyConstants.ADMIN_ROLE;
import static acmecollege.utility.MyConstants.PROFESSOR_SUBRESOURCE_NAME;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_ID_ELEMENT;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_ID_PATH;

import java.util.List;
import java.util.Set;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.security.enterprise.SecurityContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import acmecollege.ejb.ACMECollegeService;
import acmecollege.entity.CourseRegistration;
import acmecollege.entity.Professor;

@Path(PROFESSOR_SUBRESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProfessorResource {
private static final Logger LOG = LogManager.getLogger();
	
	@EJB
	protected ACMECollegeService service;
	
	@Inject
	protected SecurityContext sc;
	
	@GET
	@RolesAllowed({ADMIN_ROLE})
	public Response getAllCourses() {
		LOG.debug("retreiving all professor...");
		List<Professor> professors = service.getAllProfessor();
		Response response = Response.ok(professors).build();
		return response;
	}
	
	@GET
    @RolesAllowed({ADMIN_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response getProfessorById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int professorId) {
        LOG.debug("Try to retrieve a specific professor with id = {} " + professorId);
        Response response = null;
        Professor professor = service.getProfessorById(professorId);
        // Check if the professor is not found
        if (professor == null) {
            response = Response.status(Status.NOT_FOUND).build();
        } else {
        	response = Response.status(Status.OK).entity(professor).build();
        }
        return response;
    }
    @POST
    @RolesAllowed({ADMIN_ROLE})
    public Response createProfessor(Professor newProfessor) {
    	LOG.debug("Creating a new professor", newProfessor);
    	Professor professor = service.createProfessor(newProfessor);
        Response response = Response.ok(professor).build();
        return response;
    }

    @DELETE
    @RolesAllowed({ADMIN_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response deleteProfessor(@PathParam(RESOURCE_PATH_ID_ELEMENT) int professorId) {
        LOG.debug("Deleting professor with id = {}", professorId);
        Professor professor = service.deleteProfessorById(professorId);
        Response response = Response.ok(professor).build();
        return response;
    }

    @PUT
    @RolesAllowed({ADMIN_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response updateProfessor(@PathParam(RESOURCE_PATH_ID_ELEMENT) int professorId, Professor updatingProfessor) {
        LOG.debug("Updating a specific professor with id = {}", professorId);
        Professor updatedProfessor = service.updateProfessor(professorId, updatingProfessor);
        Response response = Response.ok(updatedProfessor).build();
        return response;
    }
    
    @GET
    @RolesAllowed({ADMIN_ROLE})
    @Path("/{id}/courseregistration")
    public Response getProfessorCourseRegistration(@PathParam(RESOURCE_PATH_ID_ELEMENT) int professorId) {
    	LOG.debug("Getting course registrations for a professor = {}", professorId);
    	Set<CourseRegistration> crs = service.getProfessorCourseRegistration(professorId);
    	Response response = Response.ok(crs).build();
    	return response;
    }
}
