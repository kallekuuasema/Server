package kalle.server;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.QueryParam;
import javax.ws.rs.GET;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
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
		//Validate request
		if(null==scoreMessage){
			return Response.status(HttpServletResponse.SC_BAD_REQUEST).build();
		}
		if(null==scoreMessage.playerId || scoreMessage.playerId.length() == 0){
			return Response.status(HttpServletResponse.SC_BAD_REQUEST).build();
		}
		if(null==scoreMessage.gameName || scoreMessage.gameName.length() == 0){
			return Response.status(HttpServletResponse.SC_BAD_REQUEST).build();
		}
		if(0==scoreMessage.score ){
			return Response.status(HttpServletResponse.SC_BAD_REQUEST).build();
		}
		
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
    public Response getHighScores(@QueryParam("playerName") String playerName, @QueryParam("playerId") String playerId, @QueryParam("gameName") String gameName, @QueryParam("score") long score) {
		
		//Compose query filters
		Vector<Filter> queryFilters = new Vector<Filter>();
		if(null!=playerName && playerName.length() > 0){
			queryFilters.add(new FilterPredicate("playerName", FilterOperator.EQUAL, playerName));
		}
		if(null!=playerId && playerId.length() > 0){
			queryFilters.add(new FilterPredicate("playerId", FilterOperator.EQUAL, playerId));
		}
		if(null!=gameName && gameName.length() > 0){
			queryFilters.add(new FilterPredicate("gameName", FilterOperator.EQUAL, gameName));
		}
		if(0!=score){
			queryFilters.add(new FilterPredicate("score", FilterOperator.EQUAL, score));
		}
		
		//Return bad request for empty queries
		if(queryFilters.size() == 0){
			return Response.status(HttpServletResponse.SC_BAD_REQUEST).build();
		}
		
		//Find all scores from datastore
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		PreparedQuery preparedQuery;

		if(queryFilters.size() == 1){
			Query query = new Query("Score").setFilter(queryFilters.get(0));
			preparedQuery = datastore.prepare(query);
		}else{
			Query query = new Query("Score").setFilter(CompositeFilterOperator.and(queryFilters));
			preparedQuery = datastore.prepare(query);
		}
		
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
			
			return Response.status(HttpServletResponse.SC_OK).type(MediaType.APPLICATION_JSON).entity(scoreEntryList).build();
		}else{
			//Return not found
			return Response.status(HttpServletResponse.SC_NOT_FOUND).build();
		}
    }
	
	@Produces(MediaType.TEXT_PLAIN)
    @DELETE
    public Response deleteScores(@QueryParam("playerId") String playerId, @QueryParam("gameName") String gameName) {
		//Compose query filters
		Vector<Filter> queryFilters = new Vector<Filter>();
		if(null!=playerId && playerId.length() > 0){
			queryFilters.add(new FilterPredicate("playerId", FilterOperator.EQUAL, playerId));
		}
		if(null!=gameName && gameName.length() > 0){
			queryFilters.add(new FilterPredicate("gameName", FilterOperator.EQUAL, gameName));
		}
		
		//Return bad request if both playerId and gameName are missing
		if(queryFilters.size() != 2){
			return Response.status(HttpServletResponse.SC_BAD_REQUEST).build();
		}
		
		//Find all scores from datastore
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		Query query = new Query("Score").setFilter(CompositeFilterOperator.and(queryFilters));
		PreparedQuery preparedQuery = datastore.prepare(query);
		
		java.util.List<Entity> result = preparedQuery.asList(FetchOptions.Builder.withDefaults());

		if(null!=result){
			for(int index=0; index < result.size(); ++index){
				Entity tmp = result.get(index);
				datastore.delete(tmp.getKey());
			}			
			return Response.status(HttpServletResponse.SC_OK).build();
		}else{
			//Return not found
			return Response.status(HttpServletResponse.SC_NOT_FOUND).build();
		}
    }
}