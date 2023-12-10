/**
 * Resource file for entity Professor
 */
package acmecollege.rest.resource;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.security.enterprise.SecurityContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import static acmecollege.utility.MyConstants.ADMIN_ROLE;
import static acmecollege.utility.MyConstants.USER_ROLE;
import javax.ws.rs.core.Response.Status;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.soteria.WrappingCallerPrincipal;

import acmecollege.ejb.ACMECollegeService;
import acmecollege.entity.Professor;
import acmecollege.entity.SecurityUser;
import acmecollege.entity.Student;
import acmecollege.entity.StudentClub;
import static acmecollege.utility.MyConstants.PROFESSOR_SOURCE_NAME;
import static acmecollege.utility.MyConstants.ADMIN_ROLE;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_ID_ELEMENT;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_ID_PATH;

/**
 * @author Mengya Shi
 *
 */
@Path(PROFESSOR_SOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProfessorResource {
    private static final Logger LOG = LogManager.getLogger();

    @EJB
    protected ACMECollegeService service;

    @Inject
    protected SecurityContext sc;
    @GET
    public Response getAllProfessor() {
        LOG.debug("Retrieving all professors...");
        List<Professor> professors = service.getAllProfessor();
        LOG.debug("Student clubs found = {}", professors);
        Response response = Response.ok(professors).build();
        return response;     
    }
    
    @GET
    @RolesAllowed({ ADMIN_ROLE })
    @Path("/{professorId}")
    public Response getProfessorById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int professorId) {
        LOG.debug("Try to retrieve a specific professor with ID " + professorId);
        Response response;
        // Check if the caller is in the ADMIN_ROLE
        if (!sc.isCallerInRole(ADMIN_ROLE)) {
            return Response.status(Status.FORBIDDEN).build();
        }
        Professor professor = service.getProfessorById(professorId);
        // Check if the professor is not found
        if (professor == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        // Return the professor if found
        response = Response.status(Status.OK).entity(professor).build();
        return response;
    }
    @POST
    @RolesAllowed({ADMIN_ROLE})
    public Response addProfessor(Professor newProfessor) {
    	LOG.debug("Adding professor", newProfessor);
    	Professor tempProfessor = service.createProfessor(newProfessor);
        Response  response = Response.ok(tempProfessor).build();
        return response;
    }

    @DELETE
    @RolesAllowed({ADMIN_ROLE})
    @Path("/{professorId}")
    public Response deleteProfessor(@PathParam("professorId") int professorId) {
        LOG.debug("Deleting professor with id = {}", professorId);
        Professor professor = service.deleteProfessorById(professorId);
        Response response = Response.ok(professor).build();
        return response;
    }

    @RolesAllowed({ADMIN_ROLE})
    @PUT
    @Path("/{professorId}")
    public Response updateProfessor(@PathParam("professorId") int professorId, Professor updatingProfessor) {
        LOG.debug("Updating a specific professor with id = {}", professorId);
        Professor updatedProfessor = service.updateProfessor(professorId, updatingProfessor);
        Response response = Response.ok(updatedProfessor).build();
        return response;
    }

}