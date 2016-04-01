package com.twitter;

import twitter4j.GeoLocation;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;

public class TwitterStreamer implements StatusListener {
	
	@Override
	public void onStatus(Status status) {
		GeoLocation geoLocation = status.getGeoLocation();
		if (geoLocation != null) {
			String message = "@" + status.getUser().getScreenName() + " - " + status.getText() + " - "
					+ status.getGeoLocation();

			System.out.println(message);
			try {
				TwitterWebSocket.onMessage(message,status);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
		// System.out.println("Got a status deletion notice id:" +
		// statusDeletionNotice.getStatusId());
	}

	@Override
	public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
		// System.out.println("Got track limitation notice:" +
		// numberOfLimitedStatuses);
	}

	@Override
	public void onScrubGeo(long userId, long upToStatusId) {
		// System.out.println("Got scrub_geo event userId:" + userId + "
		// upToStatusId:" + upToStatusId);
	}

	@Override
	public void onStallWarning(StallWarning warning) {
		// System.out.println("Got stall warning:" + warning);
	}

	@Override
	public void onException(Exception ex) {
		ex.printStackTrace();
	}

}
