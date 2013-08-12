package com.hexadeck.hattoss;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class RankingActivity extends ListActivity {
	private ListView lv;
	private TextView tv;
	public static final LinkedList<String> RANKING = new LinkedList<String>();
	BigDecimal newResult = null;
	private static final String TAG = "RankingActivity";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ranking);
		// lv = (ListView) findViewById(android.R.id.list);
		tv = (TextView) findViewById(R.id.result);
		Button btnLeft = (Button) findViewById(R.id.retry_button);
		Button btnRight = (Button) findViewById(R.id.tutorial_button);
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

		Time time = new Time("Asia/Tokyo");
		time.setToNow();
		String date = time.year + "/" + (time.month + 1) + "/" + time.monthDay
				+ "　" + time.hour + ":" + String.format("%1$02d", time.minute);

		// 計測結果があれば
		if (newResult != null) {
			boolean addResult = false;
			// リストが空なら結果を追加
			if (RANKING.size() == 0) {
				RANKING.add(newResult + "m　" + date);
			} else {
				for (int i = 0; i < RANKING.size(); i++) {
					int index = RANKING.get(i).indexOf("m");
					BigDecimal defBd = new BigDecimal(RANKING.get(i).substring(
							0, index));
					// i番目のデータより大きければiに挿入
					if (defBd.compareTo(newResult) <= 0) {
						RANKING.add(i, newResult + "m　" + date);
						// 追加フラグon
						addResult = true;
						break;
					}
				}
				// 追加されていなければ最後に追加
				if (addResult == false) {
					RANKING.addLast(newResult + "m　" + date);
				}
				// TOP10落ちデータ削除
				if (10 < RANKING.size()) {
					RANKING.remove(10);
				}
			}
		}
		LinkedList<String> adapterList = new LinkedList<String>();
		String prize = null;
		for (int i = 0; i < RANKING.size(); i++) {
			if (i == 0) {
				prize = "st";
			} else if (i == 1) {
				prize = "nd";
			} else if (i == 2) {
				prize = "rd";
			} else {
				prize = "th";
			}
			adapterList.add(i + 1 + prize + " : " + RANKING.get(i));
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_expandable_list_item_1, adapterList);
		setListAdapter(adapter);
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
					finish();
				} else {
					// インテントのインスタンス生成
					Intent intent2 = new Intent(RankingActivity.this,
							ReadyActivity.class);
					// 次画面のアクティビティ起動
					startActivity(intent2);
					finish();
				}
				break;
			case R.id.tutorial_button:
				intent = new Intent(RankingActivity.this,
						TutorialActivity.class);
				// 遷移先のアクティビティを起動させる
				startActivity(intent);
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
				finish();
			} else {
				// インテントのインスタンス生成
				Intent intent2 = new Intent(RankingActivity.this,
						ReadyActivity.class);
				// 次画面のアクティビティ起動
				startActivity(intent2);
				finish();
			}
			return true;
		}
		return false;
	}
}
