package com.hexadeck.hattoss.tweet;

import java.io.File;

import com.hexadeck.hattoss.R;
import com.hexadeck.hattoss.RankingActivity;
import com.hexadeck.hattoss.ReadyActivity;
import com.hexadeck.hattoss.ResultActivity;
import com.hexadeck.hattoss.R.id;
import com.hexadeck.hattoss.R.layout;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.media.ImageUpload;
import twitter4j.media.ImageUploadFactory;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Tweet extends Activity {

	private String CONSUMER_KEY = "qw2nzIMl06HNIcQyVvHndw";
	private String CONSUMER_SECRET = "mfuEiBqC9kyN55xdQ8BK5fZfMO0yH25kaPpNQTmRjU";
	// ハッシュタグ追加
	private String defoTEXT = "I threw my phone up "
			+ ReadyActivity.getResultText() + "m" + "! #HatTOSS!!";
	private String mainTEXT = null;

	protected void onCreate(Bundle bundle) {

		super.onCreate(bundle);
		setContentView(R.layout.tweet);

		final EditText edit = (EditText) findViewById(R.id.editText1);
		edit.setText(defoTEXT);
		edit.setMaxLines(3);
		edit.setSelection(0);

		final Button tweetbutton = (Button) findViewById(R.id.tweet);
		tweetbutton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Log.d("tweet", "onClick");
				// クリック時の処理
				SpannableStringBuilder sb = (SpannableStringBuilder) edit
						.getText();
				mainTEXT = sb.toString();
				AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
					@Override
					protected void onPreExecute() {
						tweetbutton.setClickable(false);
					}

					@Override
					protected Void doInBackground(Void... params) {

						try {
							tweet();
						} catch (TwitterException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						Intent intent = new Intent(Tweet.this,
								RankingActivity.class);
						intent.putExtra("newresult", 0);
						startActivity(intent);
						finish();
					}
				};
				task.execute(); // ここでは何も渡さない
			}
		});
		Button cancelbutton = (Button) findViewById(R.id.tweet_cancel);
		cancelbutton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(Tweet.this, RankingActivity.class);
				startActivity(intent);
				finish();
			}
		});
	}

	public void tweet() throws TwitterException {
		// 記録していた設定ファイルを読み込む。
		SharedPreferences pref = getSharedPreferences("Twitter_seting",
				MODE_PRIVATE);

		// 設定ファイルからoauth_tokenとoauth_token_secretを取得。
		String oauthToken = pref.getString("oauth_token", "");
		String oauthTokenSecret = pref.getString("oauth_token_secret", "");

		ConfigurationBuilder confbuilder = new ConfigurationBuilder();

		confbuilder.setOAuthAccessToken(oauthToken)
				.setOAuthAccessTokenSecret(oauthTokenSecret)
				.setOAuthConsumerKey(CONSUMER_KEY)
				.setOAuthConsumerSecret(CONSUMER_SECRET)
				.setMediaProvider("TWITTER");

		ImageUpload imageUpload = new ImageUploadFactory(confbuilder.build())
				.getInstance();

		// Twitter twitter = new
		// TwitterFactory(confbuilder.build()).getInstance();

		String path = getApplication().getFilesDir().getAbsolutePath()
				+ "/hatTossImgTmp.jpg";
		Log.d("path = ", path);
		File file = new File(path);

		// 任意の文字列を引数にして、ツイート。
		if (path != null) {
			imageUpload.upload(file, mainTEXT);
		}
	}

}