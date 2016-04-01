package com.twitter;
 
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.fasterxml.jackson.databind.ObjectMapper;
 
@Path("load")
public class LoadTweetsService {
 
	@GET
	@Path("/tweets")
	public Response getMsg() throws Exception {
		DynamoDBMapper mapper = new DynamoDBMapper(TwitterWebSocket.client);
		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        List<Tweet> tweetList = mapper.scan(Tweet.class, scanExpression);
        ObjectMapper jacksonObjectMapper = new ObjectMapper();
        String tweetListJson = jacksonObjectMapper.writeValueAsString(tweetList);
 
		return Response.status(200).entity(tweetListJson).build();
	}
	
	@GET
	@Path("/tweetsByKeyword")
	public Response getRelatedTweets(@QueryParam("keyword") String keyword) throws Exception {
		DynamoDBMapper mapper = new DynamoDBMapper(TwitterWebSocket.client);
		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
		scanExpression.addFilterCondition("Tweet_Text", 
                new Condition()
                   .withComparisonOperator(ComparisonOperator.CONTAINS)
                   .withAttributeValueList(new AttributeValue().withS(keyword)));
        List<Tweet> tweetList = mapper.scan(Tweet.class, scanExpression);
        ObjectMapper jacksonObjectMapper = new ObjectMapper();
        String tweetListJson = jacksonObjectMapper.writeValueAsString(tweetList);
 
		return Response.status(200).entity(tweetListJson).build();
	}
	
	
	@GET
	@Path("/countByKeyword")
	public Response getCountByKeywords() throws Exception {
		DynamoDBMapper mapper = new DynamoDBMapper(TwitterWebSocket.client);
		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        List<Tweet> tweetList = mapper.scan(Tweet.class, scanExpression);
        
        for (Tweet tweet : tweetList) {
			for (int i = 0; i < TwitterWebSocket.s.length; i++) {
				if(tweet.getTweetText().contains(TwitterWebSocket.s[i])){
					TwitterWebSocket.data[i]++;
				}
			}
		}
        
        ObjectMapper jacksonObjectMapper = new ObjectMapper();
        String dataJson = jacksonObjectMapper.writeValueAsString(TwitterWebSocket.data);
 
		return Response.status(200).entity(dataJson).build();
	}
 
}