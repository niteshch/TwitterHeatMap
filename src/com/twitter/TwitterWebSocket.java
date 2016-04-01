package com.twitter;

import java.io.IOException;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.fasterxml.jackson.databind.ObjectMapper;

import twitter4j.Status;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

@ServerEndpoint("/stream")
public class TwitterWebSocket {
	static Session session;
	static TwitterStream twitterStream;
	static AWSCredentials credentials = null;
	static AmazonDynamoDBClient client = null;
	static String[] s = { "Game", "Birthday", "Weekend", "Love", "Work", "Coffee","the","a","at","our","we" };
	static int[] data = new int[s.length];

	static {
		try {
			credentials = new PropertiesCredentials(
					TwitterWebSocket.class.getResourceAsStream("AwsCredentials.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		client = new AmazonDynamoDBClient(credentials);
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true).setOAuthConsumerKey("VoKVyWzM9UlHctYWOOAMIaQ4e")
				.setOAuthConsumerSecret("NeA1pB4L3N5oPbi2CqMYIiuWYw57b7sOb3wbY9z7WMKyjlU5Rr")
				.setOAuthAccessToken("2315925331-SY865z1XO2waCGJwtegBQKSlWK9gM1PKK5S98V8")
				.setOAuthAccessTokenSecret("QKsx6GJiJFYmPI3iy8fkJztfWluJ4dHH2Anp0FuCf1Ke8");

		twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
		twitterStream.addListener(new TwitterStreamer());
		twitterStream.sample();
	}

	@OnOpen
	public void open(Session session) throws Exception {
		TwitterWebSocket.session = session;
	}

	@OnClose
	public void close(Session session) {
	}

	@OnError
	public void onError(Throwable error) {
	}

	@OnMessage
	public static void onMessage(String message, Status status) throws Exception {
		if (session != null && session.isOpen()) {
			Tweet tweet = createTweetItem(status);
			for (int i = 0; i < TwitterWebSocket.s.length; i++) {
				if (tweet.getTweetText().contains(TwitterWebSocket.s[i])) {
					TwitterWebSocket.data[i]++;
				}
			}
			tweet.setCountArray(TwitterWebSocket.data);
			ObjectMapper jacksonObjectMapper = new ObjectMapper();
			String tweetString = jacksonObjectMapper.writeValueAsString(tweet);
			
			session.getBasicRemote().sendText(tweetString);


		}
	}

	private static Tweet createTweetItem(Status status) {
		Tweet tweet = new Tweet();
		tweet.setTweetText(status.getText());
		tweet.setTweetUser(status.getUser().getScreenName());
		tweet.setTweetLatitude(status.getGeoLocation().getLatitude());
		tweet.setTweetLongitude(status.getGeoLocation().getLongitude());

		DynamoDBMapper mapper = new DynamoDBMapper(client);
		mapper.save(tweet);
		return tweet;
	}

}
