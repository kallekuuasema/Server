package kalle.server;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.QueryParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/scores")
public class Scores {

	@Consumes(MediaType.TEXT_PLAIN)
	@POST
	public void createScore(){
		
	}

	@Produces(MediaType.TEXT_PLAIN)
    @GET
    public String getScores(@QueryParam("playerId") String playerId) {
        return "got " + playerId;
    }
}