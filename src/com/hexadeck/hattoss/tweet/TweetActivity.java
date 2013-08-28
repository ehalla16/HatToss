package com.hexadeck.hattoss.tweet;

import com.hexadeck.hattoss.R;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class TweetActivity extends Activity {

	private Twitter twitter = null;
	private RequestToken requestToken = null;

	private String CONSUMER_KEY = "qw2nzIMl06HNIcQyVvHndw";
	private String CONSUMER_SECRET = "mfuEiBqC9kyN55xdQ8BK5fZfMO0yH25kaPpNQTmRjU";

	private String hatTossStatus;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.result);

		// プリファレンス設定
		SharedPreferences pref = getSharedPreferences("Twitter_seting",
				MODE_PRIVATE);
		// プリファレンス書き込み
		hatTossStatus = pref.getString("status", "");

		Button loginbutton = (Button) findViewById(R.id.tweet);
		loginbutton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {

				// もう設定されているか？
				if (isConnected(hatTossStatus)) {

					// disconnectTwitter();

					Intent intent2 = new Intent();
					intent2.setClassName("com.hexadeck.hattoss",
							"com.hexadeck.hattoss.Tweet");
					intent2.setAction(Intent.ACTION_VIEW);
					startActivityForResult(intent2, 0);

				} else {

					try {
						connectTwitter();
					} catch (TwitterException e) {
						// showToast(R.string.nechatter_connect_error);
					}
				}

			}
		});

//		Button logoutbutton = (Button) findViewById(R.id.logout);
//		logoutbutton.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View view) {
//
//				disconnectTwitter();
//				try {
//					connectTwitter();
//				} catch (TwitterException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//
//		});

	}

	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {

		if (resultCode == RESULT_OK) {
			super.onActivityResult(requestCode, resultCode, intent);

			AccessToken accessToken = null;

			try {
				accessToken = twitter.getOAuthAccessToken(requestToken, intent
						.getExtras().getString("oauth_verifier"));

				SharedPreferences pref = getSharedPreferences("Twitter_seting",
						MODE_PRIVATE);

				SharedPreferences.Editor editor = pref.edit();
				editor.putString("oauth_token", accessToken.getToken());
				editor.putString("oauth_token_secret",
						accessToken.getTokenSecret());
				editor.putString("status", "available");

				editor.commit();

				// つぶやくページへGO
				Intent intent2 = new Intent(this, Tweet.class);
				this.startActivityForResult(intent2, 0);

				// finish();
			} catch (TwitterException e) {
				// showToast(R.string.nechatter_connect_error);
			}
		}
	}

	private void connectTwitter() throws TwitterException {

		// 参考:http://groups.google.com/group/twitter4j/browse_thread/thread/d18c179ba0d85351
		// 英語だけど読んでね！
		ConfigurationBuilder confbuilder = new ConfigurationBuilder();

		confbuilder.setOAuthConsumerKey(CONSUMER_KEY).setOAuthConsumerSecret(
				CONSUMER_SECRET);

		twitter = new TwitterFactory(confbuilder.build()).getInstance();

		String CALLBACK_URL = "myapp://oauth";
		// requestTokenもクラス変数。
		try {
			requestToken = twitter.getOAuthRequestToken(CALLBACK_URL);
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 認証用URLをインテントにセット。
		// TwitterLoginはActivityのクラス名。
		Intent intent = new Intent(this, TwitterLogin.class);
		intent.putExtra("auth_url", requestToken.getAuthorizationURL());

		// アクティビティを起動
		this.startActivityForResult(intent, 0);

	}

	final private boolean isConnected(String nechatterStatus) {
		if (nechatterStatus != null && nechatterStatus.equals("available")) {
			return true;
		} else {
			return false;
		}
	}

	public void disconnectTwitter() {

		SharedPreferences pref = getSharedPreferences("Twitter_seting",
				MODE_PRIVATE);

		SharedPreferences.Editor editor = pref.edit();
		editor.remove("oauth_token");
		editor.remove("oauth_token_secret");
		editor.remove("status");

		editor.commit();

		// finish();
	}

}