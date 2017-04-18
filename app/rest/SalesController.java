package rest;

import logic.sales.SalesLogic;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;

/**
 * Created by Fabian on 08.04.17.
 */
public class SalesController extends Controller {
    private SalesLogic salesLogic;

    @Inject
    public SalesController(SalesLogic salesLogic){
        this.salesLogic = salesLogic;
    }

    @Transactional
    public Result buyTicket(Integer userId, Integer ticketCategory){
        if (salesLogic.buyTicket(userId, ticketCategory) != null){
            return ok("Ticket bought");
        }
        return badRequest("Ticket not bought");
    }
}