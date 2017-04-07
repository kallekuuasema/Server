package kalle.server;

import java.util.List;

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
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.FetchOptions.Builder;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import java.util.Arrays;
import java.util.Vector;

import javax.ws.rs.core.Response;
import javax.servlet.http.HttpServletResponse;

@Path("/scores")
public class Scores {

	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	@POST
	public Response createScore(ScoreMessage scoreMessage){
		//Find entry from datastore
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Query query = new Query("Score").setFilter(CompositeFilterOperator.and(new FilterPredicate("playerId", FilterOperator.EQUAL, scoreMessage.playerId), new FilterPredicate("gameName", FilterOperator.EQUAL, scoreMessage.gameName)));
		
		PreparedQuery preparedQuery = datastore.prepare(query);
		Entity result = preparedQuery.asSingleEntity();

		if(null!=result){
			if((long)result.getProperty("score") < scoreMessage.score){
				result.setProperty("score", scoreMessage.score);
				datastore.put(result);
				
				//return ok
				return Response.status(HttpServletResponse.SC_OK).build();
			}else{
				return Response.status(HttpServletResponse.SC_NOT_MODIFIED).build();
			}
		}else{
			//Create new score entry
			Entity scoreEntity = new Entity("Score");
			scoreEntity.setProperty("playerName", scoreMessage.playerName);
			scoreEntity.setProperty("playerId", scoreMessage.playerId);
			scoreEntity.setProperty("gameName", scoreMessage.gameName);
			scoreEntity.setProperty("score", scoreMessage.score);
			datastore.put(scoreEntity);

			//Return created
			return Response.status(HttpServletResponse.SC_CREATED).build();
		}
	}

	@Produces(MediaType.APPLICATION_JSON)
    @GET
    public Response getScores(@QueryParam("playerId") String playerId, @QueryParam("gameName") String gameName) {
		//Find all scores from datastore
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Query query = new Query("Score").setFilter(CompositeFilterOperator.and(new FilterPredicate("playerId", FilterOperator.EQUAL, playerId), new FilterPredicate("gameName", FilterOperator.EQUAL, gameName)));
		PreparedQuery preparedQuery = datastore.prepare(query);
		
		Entity result = preparedQuery.asSingleEntity();
		
		ScoreMessage ret = new ScoreMessage();
		ret.playerName = (String)result.getProperty("playerName");
		ret.playerId = (String)result.getProperty("playerId");
		ret.gameName = (String)result.getProperty("gameName");
		ret.score = (long)result.getProperty("score");
		
		if(null!=result){
			//Return scores as array
			return Response.ok().type(MediaType.APPLICATION_JSON).entity(ret).build();
		}else{
			//Return not found
			return Response.status(HttpServletResponse.SC_NOT_FOUND).build();
		}
    }
	
	@Produces(MediaType.APPLICATION_JSON)
    @GET
	@Path("/highscores")
    public Response getHighScores(@QueryParam("gameName") String gameName) {
		//Find all scores from datastore
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Query query = new Query("Score").setFilter(new FilterPredicate("gameName", FilterOperator.EQUAL, gameName));
		PreparedQuery preparedQuery = datastore.prepare(query);
		
		java.util.List<Entity> result = preparedQuery.asList(FetchOptions.Builder.withDefaults());

		if(null!=result){
			//Return scores as array
			Vector<ScoreMessage> scoreEntryList = new Vector<ScoreMessage>();
			for(int index=0; index < result.size(); ++index){
				Entity tmp = result.get(index);
				ScoreMessage scoreEntry = new ScoreMessage();
				scoreEntry.playerName = (String)tmp.getProperty("playerName");
				scoreEntry.playerId = (String)tmp.getProperty("playerId");
				scoreEntry.gameName = (String)tmp.getProperty("gameName");
				scoreEntry.score = (long)tmp.getProperty("score");
				scoreEntryList.add(scoreEntry);
			}
			
			return Response.ok().type(MediaType.APPLICATION_JSON).entity(scoreEntryList).build();
		}else{
			//Return not found
			return Response.status(HttpServletResponse.SC_NOT_FOUND).build();
		}
    }
}