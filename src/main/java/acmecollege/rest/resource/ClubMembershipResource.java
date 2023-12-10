package acmecollege.rest.resource;

import static acmecollege.utility.MyConstants.ADMIN_ROLE;
import static acmecollege.utility.MyConstants.CLUB_MEMBERSHIP_RESOURCE_NAME;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_ID_ELEMENT;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_ID_PATH;
import static acmecollege.utility.MyConstants.USER_ROLE;

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
import javax.ws.rs.core.Response.Status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import acmecollege.ejb.ACMECollegeService;
import acmecollege.entity.ClubMembership;
import acmecollege.entity.MembershipCard;

@Path(CLUB_MEMBERSHIP_RESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ClubMembershipResource {
	
    private static final Logger LOG = LogManager.getLogger();
    
    @EJB
    protected ACMECollegeService service;

    @Inject
    protected SecurityContext sc;
    
    
    @GET
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
    public Response getAllClubMembership() {
        LOG.debug("Retrieving all ClubMembership...");
        List<ClubMembership> clubMemberships = service.getAllClubMemberships();
        LOG.debug("ClubMemberships found = {}", clubMemberships);
        Response response = Response.ok(clubMemberships).build();        
        return Response.ok(clubMemberships).build();
    }

    @GET
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response getClubMembershipById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
        LOG.debug("try to retrieve specific ClubMembership " + id);
        Response response = null;
        ClubMembership clubMembership = service.getClubMembershipById(id);
        response = Response.status(clubMembership == null ? Status.NOT_FOUND : Status.OK).entity(clubMembership).build();
        return response;
    }

//    @POST
//    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
//    @Path("/studentClub/{studentClubId}")
//    public Response createNewClubMembership(@PathParam("studentClubId") int studentClubId, ClubMembership clubMembership) {
//    	ClubMembership newClubMembership = service.createNewClubMembership(studentClubId, clubMembership);
//        return Response.ok(newClubMembership).build();
//    }addNewStudentClub
    
    @POST
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
    @Path("/studentClub/{studentClubId}")
    public Response createNewClubMembership(@PathParam("studentClubId") int studentClubId, ClubMembership clubMembership) {
    	ClubMembership newClubMembership = service.createNewClubMembership(studentClubId, clubMembership);
        return Response.ok(newClubMembership).build();
    }
    

    @PUT
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
    @Path("/{id}/studentClub/{studentClubId}")
    public Response updateStudentClubForClubMembership(@PathParam("id") int id, @PathParam("studentClubId") int studentClubId) {
        LOG.debug("Updating a specific clubmembership with id = {}", id);
        Response response = null;
        ClubMembership updatedClubMembership = service.updateStudentClubForClubMembership(id, studentClubId);
        if (updatedClubMembership != null) {
            return Response.ok(updatedClubMembership).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
    
    @DELETE
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response deleteClubMembership(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
        LOG.debug("Deleting clubmembership with id = {}", id);
        ClubMembership mc = service.deleteClubMembershipById(id);
        return Response.ok(mc).build();
    }

}
