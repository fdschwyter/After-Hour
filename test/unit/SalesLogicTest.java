package unit;

import config.StartupConfiguration;
import config.StartupConfigurationMock;
import dal.events.EventsRepository;
import dal.events.EventsRepositoryMock;
import dal.tickets.TicketRepository;
import dal.tickets.TicketRepositoryMock;
import dal.users.UsersRepository;
import dal.users.UsersRepositoryMock;
import logic.sales.SalesLogic;
import models.exceptions.ServerException;
import models.exceptions.TicketAlreadyBoughtException;
import models.tickets.Ticket;
import org.junit.Before;
import org.junit.Test;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.test.WithApplication;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.junit.Assert.assertNotNull;
import static play.inject.Bindings.bind;

/**
 * Created by Fabian on 10.05.17.
 */
public class SalesLogicTest extends WithApplication {
    private SalesLogic salesLogic;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder()
                .overrides(bind(UsersRepository.class).to(UsersRepositoryMock.class))
                .overrides(bind(EventsRepository.class).to(EventsRepositoryMock.class))
                .overrides(bind(TicketRepository.class).to(TicketRepositoryMock.class))
                .overrides(bind(StartupConfiguration.class).to(StartupConfigurationMock.class).eagerly())
                .build();
    }


   @Before
    public void setup(){
       salesLogic = this.app.injector().instanceOf(SalesLogic.class);

   }

   @Test
   public void buyTicket() throws ParseException, ServerException {
        Ticket ticket = salesLogic.buyTicket(2,1, dateFormat.parse("2017-4-22"));
        assertNotNull(ticket);
   }

   @Test (expected = TicketAlreadyBoughtException.class)
    public void buyTicketAlreadyBought() throws ServerException {
        salesLogic.buyTicket(1,1);
   }

}
