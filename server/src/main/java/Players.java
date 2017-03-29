package kalle.server;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.QueryParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/players")
public class Players {

	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	@POST
	public String createPlayer(){
		return "id123";
	}

	@Produces(MediaType.TEXT_PLAIN)
    @GET
    public String getPlayer(@QueryParam("playerName") String playerName) {
        return "got "+playerName;
    }
}