package integration.database;

import com.google.inject.Guice;
import dal.events.EventsRepository;
import dal.events.EventsRepositoryJPA;
import dal.ticket_categories.TicketCategoriesRepositoryJPA;
import models.events.Event;
import models.events.TicketCategory;
import org.eclipse.jetty.util.DateCache;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import play.Environment;
import play.db.jpa.JPAApi;
import play.inject.guice.GuiceApplicationBuilder;
import play.inject.guice.GuiceApplicationLoader;
import play.test.WithApplication;

import javax.inject.Inject;
import javax.validation.constraints.AssertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

/**
 * Created by Fabian on 09.04.2017.
 */
public class TicketCategoryPersistenceTesting extends WithApplication {
    @Inject
    private JPAApi jpaApi;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");

    @Before
    public void setup() {
        GuiceApplicationBuilder builder = new GuiceApplicationLoader()
                .builder(new play.ApplicationLoader.Context(Environment.simple()));
        Guice.createInjector(builder.applicationModule()).injectMembers(this);
    }

    @After
    public void teardown() {
    }

    @Test
    public void testRegisterNewTicketCategoryWithoutEvent(){
        this.jpaApi.withTransaction(() -> {
            TicketCategoriesRepositoryJPA repository = new TicketCategoriesRepositoryJPA(jpaApi);
            try {
                TicketCategory expectedTicketCategory = new TicketCategory(null, "Studenten Ticket", "Ticket für Studenten", null, 15.00, dateFormat.parse("2017-4-20"), dateFormat.parse("2017-5-20") );
                play.Logger.info("Created TicketCategory: " + expectedTicketCategory);
                repository.registerTicketCategory(expectedTicketCategory);
                TicketCategory actualTicketCategory = repository.getTicketCategoryById(expectedTicketCategory.getId());
                assertEquals(expectedTicketCategory.getId(), actualTicketCategory.getId());
            } catch (ParseException e) {
                e.printStackTrace();
            }

        });
    }

    @Test
    public void testReferenceFromTicketCategoryToEvent(){
        this.jpaApi.withTransaction(() -> {
            TicketCategoriesRepositoryJPA ticketCategoriesRepositoryJPA = new TicketCategoriesRepositoryJPA(jpaApi);
            EventsRepositoryJPA eventsRepositoryJPA = new EventsRepositoryJPA(jpaApi);
            try {
                Event event = eventsRepositoryJPA.getEventById(1);
                TicketCategory expectedTicketCategory = new TicketCategory(null, "Studenten Ticket", "Ticket für Studenten", event , 15.00, dateFormat.parse("2017-4-20"), dateFormat.parse("2017-5-20") );
                play.Logger.info("Created TicketCategory: " + expectedTicketCategory + " for Event:" + event);
                ticketCategoriesRepositoryJPA.registerTicketCategory(expectedTicketCategory);
                assertTrue(eventsRepositoryJPA.getEventById(1).getTicketCategories().contains(expectedTicketCategory));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
    }

}
