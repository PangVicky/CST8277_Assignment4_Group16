package acmecollege.rest.resource;

import acmecollege.entity.MembershipCard;
import acmecollege.entity.Professor;
import acmecollege.entity.SecurityUser;
import acmecollege.entity.Student;
import acmecollege.entity.StudentClub;
import acmecollege.ejb.ACMECollegeService;

import static acmecollege.utility.MyConstants.ADMIN_ROLE;
import static acmecollege.utility.MyConstants.MEMBERSHIP_CARD_RESOURCE_NAME;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_ID_PATH;
import static acmecollege.utility.MyConstants.STUDENT_COURSE_PROFESSOR_RESOURCE_PATH;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_ID_ELEMENT;
import static acmecollege.utility.MyConstants.USER_ROLE;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.security.enterprise.SecurityContext;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.soteria.WrappingCallerPrincipal;

import java.util.List;

@Path(MEMBERSHIP_CARD_RESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MembershipCardResource {
	
    private static final Logger LOG = LogManager.getLogger();


    @EJB
    protected ACMECollegeService service;
    
    @Inject
    protected SecurityContext sc;

    @GET
    @RolesAllowed({ADMIN_ROLE})
    public Response getAllMembershipCards() {
        LOG.debug("Retrieving all MembershipCards...");
        List<MembershipCard> membershipCards = service.getAllMembershipCards();
        LOG.debug("MembershipCards found = {}", membershipCards);
        Response response = Response.ok(membershipCards).build();        
        return Response.ok(membershipCards).build();
    }

    @GET
    @RolesAllowed({ADMIN_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response getMembershipCardById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
        LOG.debug("try to retrieve specific MembershipCard " + id);
        Response response = null;
        MembershipCard memberCard = null;

        if (sc.isCallerInRole(ADMIN_ROLE)) {
        	memberCard = service.getMembershipCardById(id);
            response = Response.status(memberCard == null ? Status.NOT_FOUND : Status.OK).entity(memberCard).build();
        } else if (sc.isCallerInRole(USER_ROLE)) {
        	 throw new ForbiddenException("User does not have permission to access this resource");
        } else {
            response = Response.status(Status.BAD_REQUEST).build();
        }
        return response;
    }

    @POST
    @RolesAllowed({ADMIN_ROLE})
    @Path("/student/{studentId}")
    public Response createNewMembershipCard(@PathParam("studentId") int studentId, MembershipCard membershipCard) {
        MembershipCard newMembershipCard = service.createNewMembershipCard(studentId, membershipCard);
        return Response.ok(newMembershipCard).build();
    }
    
    @PUT
    @RolesAllowed({ADMIN_ROLE})
    @Path("/{membershipCardId}/clubmembership/{clubMembershipId}")
    public Response setClubMembershipCardForMembershipCard(@PathParam("membershipCardId") int membershipCardId, @PathParam("clubMembershipId") int clubMembershipId) {
        Response response = null;
        MembershipCard membershipCard = service.setClubMembershipCardForMembershipCard(membershipCardId, clubMembershipId);
        response = Response.ok(membershipCard).build();
        return response;
    }

    @PUT
    @RolesAllowed({ADMIN_ROLE})
    @Path("/{membershipCardId}/student/{studentId}")
    public Response updateMembershipCardForStudent(@PathParam("membershipCardId") int membershipCardId, @PathParam("studentId") int studentId, MembershipCard updatingMembershipCard) {
        LOG.debug("Updating a specific membership card with id = {}", membershipCardId);
        Response response = null;
        MembershipCard updatedMembershipCard = service.updateMembershipCardForStudent(membershipCardId,studentId, updatingMembershipCard);
        if (updatedMembershipCard != null) {
            return Response.ok(updatedMembershipCard).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
    
    @DELETE
    @RolesAllowed({ADMIN_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response deleteMembershipCard(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
        LOG.debug("Deleting membership card with id = {}", id);
        MembershipCard mc = service.deleteMembershipCardById(id);
        return Response.ok(mc).build();
    }
}
