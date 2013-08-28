package com.hexadeck.hattoss;

import com.hexadeck.hattoss.tweet.Tweet;
import com.hexadeck.hattoss.tweet.TwitterLogin;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

public class ResultActivity extends Activity {

	private static final String TAG = "ResultActivity";
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
		Log.d(TAG, "hatTossStatus = " + hatTossStatus);

		// doInBackgroundに渡したいデータ。final宣言した変数を用いる
		final int param1 = 0;
		final String param2 = "hoge";

		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				// 処理をしてonPostExecute()に渡すデータをインスタンスフィールドに格納
				result1 = param1;
				result2 = param2;

				// もう設定されているか？
				if (isConnected(hatTossStatus)) {
					Intent intent2 = new Intent(ResultActivity.this,
							Tweet.class);
					// 次画面のアクティビティ起動
					startActivityForResult(intent2, 0);
					finish();

				} else {

					try {
						connectTwitter();
					} catch (TwitterException e) {
						Intent intent = new Intent(ResultActivity.this,
								RankingAllActivity.class);
						intent.putExtra("tweetError", 0);
						startActivity(intent);
						finish();
					}
				}

				return null; // Void型なのでnull値を返しておく
			}

			int result1;
			String result2;

			@Override
			protected void onPostExecute(Void result) {
				System.out.println(result1);
				System.out.println(result2);
			}
		};
		task.execute(); // ここでは何も渡さない
		Log.d("", "AsyncTask終わり");
	}

	protected void onActivityResult(int requestCode, int resultCode,
			final Intent intent) {

		if (resultCode == RESULT_OK) {
			Log.d("resultCode", "resultCode = " + RESULT_OK);

			super.onActivityResult(requestCode, resultCode, intent);
			AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
				@Override
				protected Void doInBackground(Void... params) {

					AccessToken accessToken = null;

					try {
						accessToken = twitter.getOAuthAccessToken(requestToken,
								intent.getExtras().getString("oauth_verifier"));

						SharedPreferences pref = getSharedPreferences(
								"Twitter_seting", MODE_PRIVATE);

						SharedPreferences.Editor editor = pref.edit();
						editor.putString("oauth_token", accessToken.getToken());
						editor.putString("oauth_token_secret",
								accessToken.getTokenSecret());
						editor.putString("status", "available");

						editor.commit();

						// つぶやくページへGO
						Intent intent = new Intent(ResultActivity.this,
								Tweet.class);
						startActivity(intent);
						finish();
					} catch (TwitterException e) {
						Intent intent = new Intent(ResultActivity.this,
								RankingAllActivity.class);
						intent.putExtra("tweetError", 0);
						startActivity(intent);
						finish();
					}
					return null;
				}
			};
			task.execute(); // ここでは何も渡さない
		}
	}

	private void connectTwitter() throws TwitterException {

		ConfigurationBuilder confbuilder = new ConfigurationBuilder();

		confbuilder.setOAuthConsumerKey(CONSUMER_KEY).setOAuthConsumerSecret(
				CONSUMER_SECRET);

		twitter = new TwitterFactory(confbuilder.build()).getInstance();

		String CALLBACK_URL = "myapp://oauth";
		// requestTokenもクラス変数。
		try {
			requestToken = twitter.getOAuthRequestToken(CALLBACK_URL);
		} catch (TwitterException e) {
			e.printStackTrace();
		}

		// 認証用URLをインテントにセット。
		// TwitterLoginはActivityのクラス名。
		Intent intent = new Intent(this, TwitterLogin.class);
		intent.putExtra("auth_url", requestToken.getAuthorizationURL());

		// アクティビティを起動
		this.startActivityForResult(intent, 0);

	}

	final private boolean isConnected(String hatTossStatus) {
		if (hatTossStatus != null && hatTossStatus.equals("available")) {
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
