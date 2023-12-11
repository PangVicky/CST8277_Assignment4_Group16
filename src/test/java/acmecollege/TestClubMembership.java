package acmecollege;

import static acmecollege.utility.MyConstants.APPLICATION_API_VERSION;
import static acmecollege.utility.MyConstants.APPLICATION_CONTEXT_ROOT;
import static acmecollege.utility.MyConstants.DEFAULT_ADMIN_USER;
import static acmecollege.utility.MyConstants.DEFAULT_ADMIN_USER_PASSWORD;
import static acmecollege.utility.MyConstants.DEFAULT_USER;
import static acmecollege.utility.MyConstants.DEFAULT_USER_PASSWORD;
import static acmecollege.utility.MyConstants.CLUB_MEMBERSHIP_RESOURCE_NAME;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_ID_PATH;
import static acmecollege.utility.MyConstants.USER_ROLE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.security.enterprise.SecurityContext;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.logging.LoggingFeature;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.client.Entity;

import acmecollege.ejb.ACMECollegeService;
import acmecollege.entity.AcademicStudentClub;
import acmecollege.entity.ClubMembership;
import acmecollege.entity.DurationAndStatus;
import acmecollege.entity.Student;
import acmecollege.entity.StudentClub;

@SuppressWarnings("unused")

@TestMethodOrder(MethodOrderer.MethodName.class)
public class TestClubMembership {
    private static final Class<?> _thisClaz = MethodHandles.lookup().lookupClass();
    private static final Logger logger = LogManager.getLogger(_thisClaz);

    static final String HTTP_SCHEMA = "http";
    static final String HOST = "localhost";
    static final int PORT = 8080;

    // Test fixture(s)
    static URI uri;
    static HttpAuthenticationFeature adminAuth;
    static HttpAuthenticationFeature userAuth;
    private static final int Id = 1;
    private static final int studentClubId = 1;
    
    @Inject
    protected SecurityContext sc;
    
    @EJB
    protected ACMECollegeService service;


    @BeforeAll
    public static void oneTimeSetUp() throws Exception {
        logger.debug("oneTimeSetUp");
        uri = UriBuilder
            .fromUri(APPLICATION_CONTEXT_ROOT + APPLICATION_API_VERSION)
            .scheme(HTTP_SCHEMA)
            .host(HOST)
            .port(PORT)
            .build();
        adminAuth = HttpAuthenticationFeature.basic(DEFAULT_ADMIN_USER, DEFAULT_ADMIN_USER_PASSWORD);
        userAuth = HttpAuthenticationFeature.basic(DEFAULT_USER, DEFAULT_USER_PASSWORD);
    }

    protected WebTarget webTarget;
    @BeforeEach
    public void setUp() {
        Client client = ClientBuilder.newClient(
            new ClientConfig().register(MyObjectMapperProvider.class).register(new LoggingFeature()));
        webTarget = client.target(uri);
    }
    
    @Test
    @Order(1)
    public void test01_all_clubMemberships_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            //.register(userAuth)
            .register(adminAuth)
            .path(CLUB_MEMBERSHIP_RESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        List<ClubMembership> clubMemberships = response.readEntity(new GenericType<List<ClubMembership>>(){});
        assertThat(clubMemberships, is(not(empty())));
        assertThat(clubMemberships, hasSize(2));
    }
    
    @Test
    @Order(2)
    public void test02_query_clubMembership_by_Id_with_userrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(userAuth)
//            .register(adminAuth)
            .path(CLUB_MEMBERSHIP_RESOURCE_NAME+"/"+Id)
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        ClubMembership clubMembership = response.readEntity(ClubMembership.class);
        assertThat(clubMembership.getId(), is(1));
    }
    
    @Test
    @Order(3)
    public void test03_create_new_clubMembership_with_adminrole() throws JsonMappingException, JsonProcessingException {
        DurationAndStatus durationAndStatus = new DurationAndStatus();
        durationAndStatus.setDurationAndStatus(LocalDateTime.parse("2023-01-01T00:00:00"), LocalDateTime.parse("2023-12-31T23:59:59"), "1");
        Response response = webTarget
            .register(adminAuth)
            .path(CLUB_MEMBERSHIP_RESOURCE_NAME+"/studentClub/"+studentClubId)
            .request(MediaType.APPLICATION_JSON)
            .post(Entity.entity(durationAndStatus, MediaType.APPLICATION_JSON));
        assertThat(response.getStatus(), is(200));
        ClubMembership clubMembership = response.readEntity(ClubMembership.class);
        assertThat(clubMembership.getStudentClub().getId(), is(1));
    }
    
    @Test
    @Order(4)
    public void test04_update_clubMembership_with_adminrole() throws JsonMappingException, JsonProcessingException {
    	StudentClub newStudentClub = new AcademicStudentClub(); 
    	newStudentClub.setId(studentClubId);
    	newStudentClub.setAcademic(false);
    	newStudentClub.setName("Mountain Hiking Club");
        DurationAndStatus durationAndStatus = new DurationAndStatus();
        durationAndStatus.setDurationAndStatus(LocalDateTime.parse("2023-06-01T00:00:00"), LocalDateTime.parse("2023-12-31T23:59:59"), "1");
        ClubMembership updateRequest = new ClubMembership();
        updateRequest.setDurationAndStatus(durationAndStatus);
        updateRequest.setStudentClub(newStudentClub);
        updateRequest.setId(Id);
        
        Response response = webTarget
//            .register(userAuth)
            .register(adminAuth)
            .path(CLUB_MEMBERSHIP_RESOURCE_NAME+"/"+Id+"/studentClub/"+studentClubId)
            .request(MediaType.APPLICATION_JSON)
            .put(Entity.entity(updateRequest, MediaType.APPLICATION_JSON));
        assertThat(response.getStatus(), is(200));
        ClubMembership clubMembership = response.readEntity(ClubMembership.class);
        assertThat(clubMembership.getStudentClub().getId(), is(1));
    }
    
    @Test
    @Order(5)
    public void test05_delete_clubMembership_with_adminrole() throws JsonMappingException, JsonProcessingException { 
      DurationAndStatus durationAndStatus = new DurationAndStatus();
      durationAndStatus.setDurationAndStatus(LocalDateTime.parse("2023-01-01T00:00:00"), LocalDateTime.parse("2023-12-31T23:59:59"), "1");
      Response response_create = webTarget
          .register(adminAuth)
          .path(CLUB_MEMBERSHIP_RESOURCE_NAME+"/studentClub/"+studentClubId)
          .request(MediaType.APPLICATION_JSON)
          .post(Entity.entity(durationAndStatus, MediaType.APPLICATION_JSON));
      assertThat(response_create.getStatus(), is(200));
      ClubMembership clubMembership_create = response_create.readEntity(ClubMembership.class);

        Response response = webTarget
            .register(adminAuth)
            .path(CLUB_MEMBERSHIP_RESOURCE_NAME+"/"+clubMembership_create.getId())
            .request(MediaType.APPLICATION_JSON)
            .delete();
        assertThat(response.getStatus(), is(200));
        ClubMembership clubMembership_del = response.readEntity(ClubMembership.class);
        assertThat(clubMembership_del.getId(), is(clubMembership_create.getId()));
    }

    @Test
    @Order(6)
    public void test06_forbidden_update_StudentClub_for_clubMembership_with_userrole() throws JsonMappingException, JsonProcessingException { 
      DurationAndStatus durationAndStatus = new DurationAndStatus();
      durationAndStatus.setDurationAndStatus(LocalDateTime.parse("2023-01-01T00:00:00"), LocalDateTime.parse("2023-12-31T23:59:59"), "1");
      Response response = webTarget
          .register(userAuth)
//          .register(adminAuth)
          .path(CLUB_MEMBERSHIP_RESOURCE_NAME+"/studentClub/"+studentClubId)
          .request(MediaType.APPLICATION_JSON)
          .post(Entity.entity(durationAndStatus, MediaType.APPLICATION_JSON));
      assertThat(response.getStatus(), is(403));
    }
}
