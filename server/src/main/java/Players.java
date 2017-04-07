package kalle.server;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.QueryParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.PreparedQuery;
import javax.ws.rs.core.Response;
import javax.servlet.http.HttpServletResponse;

import java.util.UUID;

@Path("/players")
public class Players {

	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	@POST
	public Response createPlayer(PlayerMessage playerMessage){
		//Return bad request for empty names
		if(null==playerMessage.playerName || playerMessage.playerName.length() == 0){
			return Response.status(HttpServletResponse.SC_BAD_REQUEST).build();
		}
		
		//Create new player
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();		
		String randomUUID = ""+UUID.randomUUID();
		Entity playerEntity = new Entity("Player");
		playerEntity.setProperty("playerName", playerMessage.playerName);
		playerEntity.setProperty("playerId", randomUUID);		
		datastore.put(playerEntity);
		
		//Return player UUID
		return Response.status(HttpServletResponse.SC_CREATED).type(MediaType.TEXT_PLAIN).entity(randomUUID).build();
	}

	@Produces(MediaType.TEXT_PLAIN)
    @GET
    public Response getPlayer(@QueryParam("playerId") String playerId) {
		//Return not found for empty names
		if(null==playerId || playerId.length() == 0){
			return Response.status(HttpServletResponse.SC_NOT_FOUND).build();
		}
	
		//Find entry from datastore
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Query query = new Query("Player").setFilter(new FilterPredicate("playerId", FilterOperator.EQUAL, playerId));
		PreparedQuery preparedQuery = datastore.prepare(query);
		Entity result = preparedQuery.asSingleEntity();
		
		if(null!=result){
			//Return player name
			return Response.status(HttpServletResponse.SC_OK).type(MediaType.TEXT_PLAIN).entity((String)result.getProperty("playerName")).build();
		}else{
			//Return not found
			return Response.status(HttpServletResponse.SC_NOT_FOUND).build();
		}
    }
}