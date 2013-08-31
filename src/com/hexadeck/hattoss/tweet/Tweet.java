package com.hexadeck.hattoss.tweet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import jp.basicinc.gamefeat.ranking.android.sdk.controller.GFRankingController;

import com.hexadeck.hattoss.R;
import com.hexadeck.hattoss.RankingAllActivity;
import com.hexadeck.hattoss.ReadyActivity;

import twitter4j.TwitterException;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.media.ImageUpload;
import twitter4j.media.ImageUploadFactory;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class Tweet extends Activity {

	private String CONSUMER_KEY = "qw2nzIMl06HNIcQyVvHndw";
	private String CONSUMER_SECRET = "mfuEiBqC9kyN55xdQ8BK5fZfMO0yH25kaPpNQTmRjU";
	// ハッシュタグ追加
	private String defoTEXT = "I threw my phone up "
			+ ReadyActivity.getResultText() + "m" + "! #HatTOSS!!";
	private String mainTEXT = null;
	private ImageView imgView = null;

	protected void onCreate(Bundle bundle) {

		super.onCreate(bundle);
		setContentView(R.layout.tweet);
		InputStream in;
		try {
			in = openFileInput("hatTossImgTmp.jpg");
			Bitmap bitmap01 = BitmapFactory.decodeStream(in);
			imgView = (ImageView)findViewById(R.id.bg_pic);  
			imgView.setImageBitmap(bitmap01);
		} catch (IOException e) {
			e.printStackTrace();
		}
		

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
							Log.d("","Tweet失敗");
						}
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
					// スコア送信（ランキング用）
			            String sResult = ReadyActivity.getResultText();
						String[] gameIds = { "com.hattoss.hexadeck" };
						String[] scores = { sResult };
						GFRankingController appController = GFRankingController
								.getIncetance(Tweet.this);
						appController.sendScore(gameIds, scores);
						// スコア送信（スコア履歴用）
						appController.sendHistoryScore(gameIds, scores);
						Intent intent = new Intent(Tweet.this, RankingAllActivity.class);
						startActivity(intent);
					}
				};
				task.execute(); // ここでは何も渡さない
			}
		});
		Button cancelbutton = (Button) findViewById(R.id.tweet_cancel);
		cancelbutton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(Tweet.this, RankingAllActivity.class);
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