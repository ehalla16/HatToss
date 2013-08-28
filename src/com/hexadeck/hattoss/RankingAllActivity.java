package com.hexadeck.hattoss;

import java.math.BigDecimal;
import java.util.LinkedList;

import jp.basicinc.gamefeat.ranking.android.sdk.controller.GFRankingController;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class RankingAllActivity extends FragmentActivity {
	public static final LinkedList<String> RANKING = new LinkedList<String>();
	BigDecimal newResult = null;
	private static final String TAG = "RankingAllActivity";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ranking);
		Button btnLeft = (Button) findViewById(R.id.retry_button);
		Button btnRight = (Button) findViewById(R.id.quit_button);
		btnLeft.setOnClickListener(m_clickListener);
		btnRight.setOnClickListener(m_clickListener);
		Intent intent = getIntent();
		// TOPからの遷移
		int agreement = intent.getIntExtra("agreement", 1);
		// 計測結果からの遷移
		int newresult = intent.getIntExtra("newresult", 1);
		// tweet失敗からの遷移
		int tweetError = intent.getIntExtra("tweetError", 1);
		if (agreement == 0) {
			btnLeft.setText("TOP");
		} else if (newresult == 0) {
			newResult = new BigDecimal(ReadyActivity.getResultText());
		} else if (tweetError == 0) {
			Toast.makeText(this, "ツイートに失敗しました", Toast.LENGTH_SHORT).show();
			newResult = new BigDecimal(ReadyActivity.getResultText());
		}
		// 計測結果があれば
		if (newResult != null) {
            String dResult = ReadyActivity.getResultText();

			// スコア送信（ランキング用）
			String[] gameIds = { "com.hattoss.hexadeck" };
			String[] scores = { dResult };
			Log.d(TAG, "scores:" + scores[0]);
			GFRankingController appController = GFRankingController
					.getIncetance(RankingAllActivity.this);
			appController.sendScore(gameIds, scores);
			// スコア送信（スコア履歴用）
			appController.sendHistoryScore(gameIds, scores);
		}
		// ランキング表示
		GFRankingController.show(RankingAllActivity.this,
				"com.hattoss.hexadeck");
//		finish();
	}
	
	@Override
    public void onPause(){
    	super.onPause();
    }
    
    @Override
    public void onResume(){
    	super.onResume();
    }
	
	

	// --------------------------------------------------------------------------
	// ボタンが押された時の処理
	// --------------------------------------------------------------------------
	private OnClickListener m_clickListener = new OnClickListener() {

		public void onClick(View v) {
			Intent intent = null;
			// 遷移先のActivityを指定して、Intentを作成する
			switch (v.getId()) {
			case R.id.retry_button:
				intent = getIntent();
				int agreement = intent.getIntExtra("agreement", 1);
				if (agreement == 0) {
					intent = new Intent(RankingAllActivity.this,
							StartUpActivity.class);
					// 次画面のアクティビティ起動
					startActivity(intent);
					finish();
				} else {
					// インテントのインスタンス生成
					intent = new Intent(RankingAllActivity.this,
							ReadyActivity.class);
					// 次画面のアクティビティ起動
					startActivity(intent);
					finish();
				}
				break;
			case R.id.quit_button:
				finish();
				break;
			}
		}
	};

	// --------------------------------------------------------------------------
	// BACKキーが押された時の処理
	// --------------------------------------------------------------------------
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = getIntent();
			int agreement = intent.getIntExtra("agreement", 1);
			if (agreement == 0) {
				intent = new Intent(RankingAllActivity.this,
						StartUpActivity.class);
				// 次画面のアクティビティ起動
				startActivity(intent);
				finish();
			} else {
				// インテントのインスタンス生成
				intent = new Intent(RankingAllActivity.this,
						ReadyActivity.class);
				// 次画面のアクティビティ起動
				startActivity(intent);
				finish();
			}
			return true;
		}
		return false;
	}
}
