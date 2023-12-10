
package acmecollege;

import static acmecollege.utility.MyConstants.APPLICATION_API_VERSION;
import static acmecollege.utility.MyConstants.APPLICATION_CONTEXT_ROOT;
import static acmecollege.utility.MyConstants.DEFAULT_ADMIN_USER;
import static acmecollege.utility.MyConstants.DEFAULT_ADMIN_USER_PASSWORD;
import static acmecollege.utility.MyConstants.DEFAULT_USER;
import static acmecollege.utility.MyConstants.DEFAULT_USER_PASSWORD;
import static acmecollege.utility.MyConstants.PU_NAME;
import static acmecollege.utility.MyConstants.STUDENT_RESOURCE_NAME;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_ID_PATH;
import static acmecollege.utility.MyConstants.STUDENT_CLUB_RESOURCE_NAME;
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
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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
import acmecollege.entity.MembershipCard;
import acmecollege.entity.Student;
import acmecollege.entity.StudentClub;

@SuppressWarnings("unused")

@TestMethodOrder(MethodOrderer.MethodName.class)
public class TestStudent {
	private static final Class<?> _thisClaz = MethodHandles.lookup().lookupClass();
	private static final Logger logger = LogManager.getLogger(_thisClaz);
	
    private static final Logger LOG = LogManager.getLogger();


	static final String HTTP_SCHEMA = "http";
	static final String HOST = "localhost";
	static final int PORT = 8080;
	
    
    @PersistenceContext(name = PU_NAME)
    protected EntityManager em;

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
		uri = UriBuilder.fromUri(APPLICATION_CONTEXT_ROOT + APPLICATION_API_VERSION).scheme(HTTP_SCHEMA).host(HOST)
				.port(PORT).build();
		adminAuth = HttpAuthenticationFeature.basic(DEFAULT_ADMIN_USER, DEFAULT_ADMIN_USER_PASSWORD);
		userAuth = HttpAuthenticationFeature.basic(DEFAULT_USER, DEFAULT_USER_PASSWORD);
	}

	protected WebTarget webTarget;

	@BeforeEach
	public void setUp() {
		Client client = ClientBuilder
				.newClient(new ClientConfig().register(MyObjectMapperProvider.class).register(new LoggingFeature()));
		webTarget = client.target(uri);
	}

	@Test()
	public void test01_all_Students_with_adminrole() throws JsonMappingException, JsonProcessingException {
		Response response = webTarget
				// .register(userAuth)
				.register(adminAuth).path(STUDENT_RESOURCE_NAME).request().get();
		assertThat(response.getStatus(), is(200));
		List<Student> students = response.readEntity(new GenericType<List<Student>>() {
		});
		assertThat(students, is(not(empty())));
		assertThat(students, hasSize(1));
	}

	@Test
	public void test02_query_student_by_Id_with_adminrole() throws JsonMappingException, JsonProcessingException {
		Response response = webTarget
//				.register(userAuth)
            .register(adminAuth)
				.path(STUDENT_RESOURCE_NAME + "/" + Id).request().get();
		assertThat(response.getStatus(), is(200));
		Student student = response.readEntity(Student.class);
		assertThat(student.getId(), is(1));
	}

	@Test
	public void test03_create_new_student_with_adminrole() throws JsonMappingException, JsonProcessingException {
		Student student = new Student();
		student.setFullName("Vicky", "Pang");

		Response response = webTarget
//            .register(userAuth)
				.register(adminAuth).path(STUDENT_RESOURCE_NAME).request(MediaType.APPLICATION_JSON)
				.post(Entity.entity(student, MediaType.APPLICATION_JSON));
		assertThat(response.getStatus(), is(200));
		Student student_res = response.readEntity(Student.class);
		assertThat(student_res.getFirstName(), is("Vicky"));
	}

	@Test
	public void test04_forbidden_delete_student_with_userrole() throws JsonMappingException, JsonProcessingException {
//		Student student = new Student();
//		student.setFullName("Vicky", "Pan");
//
//		Response response_res = webTarget
////            .register(userAuth)
//			.register(adminAuth)
//            .path(STUDENT_RESOURCE_NAME)
//            .request(MediaType.APPLICATION_JSON)
//			.post(Entity.entity(student, MediaType.APPLICATION_JSON));
//		assertThat(response_res.getStatus(), is(200));
//		Student student_res = response_res.readEntity(Student.class);
//		int del_Id = student_res.getId();
//        LOG.debug("Retrieving student club with id = {}", del_Id);
		
        Response response = webTarget
              .register(userAuth)
//              .register(adminAuth)
              .path(STUDENT_CLUB_RESOURCE_NAME+"/"+Id)
              .request(MediaType.APPLICATION_JSON)
              .delete();
          assertThat(response.getStatus(), is(403));
	}

	@Test
	public void test05_query_student_by_Id_with_userrole() throws JsonMappingException, JsonProcessingException {
		Response response = webTarget.register(userAuth)
//	            .register(adminAuth)
					.path(STUDENT_RESOURCE_NAME + "/" + Id).request().get();
			assertThat(response.getStatus(), is(200));
			Student student = response.readEntity(Student.class);
			assertThat(student.getId(), is(1));
	}

	@Test
	public void test06_forbidden_getAllList_Student_with_userrole()
			throws JsonMappingException, JsonProcessingException {
		Response response = webTarget.register(userAuth)
//          .register(adminAuth)
				.path(STUDENT_RESOURCE_NAME).request(MediaType.APPLICATION_JSON)
				.get();
		assertThat(response.getStatus(), is(403));
	}
}
