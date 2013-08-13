package com.hexadeck.hattoss;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.view.Gravity;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class TutorialActivity extends Activity implements OnClickListener {
	// レイアウトの定数を省略してメンバに保持
	private final int MP = ViewGroup.LayoutParams.MATCH_PARENT;
	// インテントオプションの定数を省略してメンバに保持
	private final int FANT = Intent.FLAG_ACTIVITY_NEW_TASK // 新しいタスクでActivityを起動
			,
			FAST = Intent.FLAG_ACTIVITY_SINGLE_TOP // 現在のActivityと同じアクティビティは起動しない
			, FACT = Intent.FLAG_ACTIVITY_CLEAR_TOP // 該当のActivity上にスタックされたタスクをクリアしてから起動
			, FANA = Intent.FLAG_ACTIVITY_NO_ANIMATION // トランジションアニメーション無し
			, FANH = Intent.FLAG_ACTIVITY_NO_HISTORY // 起動時にスタックに追加しない
			, FALH = Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY // スタック内のActivityを使いまわす
			, FAER = Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS // 履歴内のActivityを使いまわす
			, FAPT = Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP // 元のActivityをタスクのトップとして起動する
			, FARF = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT // スタック内の同一Activityを最前面へ移動させる
			;

	// --------------------------------------------------------------------------
	// ライフサイクル 1 : アクティビティ初期化（iOSの、viewDidLoad）
	// --------------------------------------------------------------------------
	public void onCreate(Bundle saved_instance_state_) {
		super.onCreate(saved_instance_state_);
		setContentView(R.layout.tutorial);

		Button btnLeft = (Button) findViewById(R.id.retry_button);
		Button btnRight = (Button) findViewById(R.id.quit_button);
		btnLeft.setOnClickListener(m_clickListener);
		btnRight.setOnClickListener(m_clickListener);

		Intent intent = getIntent();
		// TOPからの遷移
		int agreement = intent.getIntExtra("agreement", 1);
		if (agreement == 0) {
			btnLeft.setText("TOP");
		}
	}

	// --------------------------------------------------------------------------
	// ライフサイクル 2 : アクティビティ開始（iOSの、viewWillAppear）
	// --------------------------------------------------------------------------
	public void onStart() {
		super.onStart();
	}

	// --------------------------------------------------------------------------
	// ライフサイクル 3 : 破棄
	// --------------------------------------------------------------------------
	public void onDestroy() {
		// ...code...

		super.onDestroy();
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
					intent = new Intent(TutorialActivity.this,
							StartUpActivity.class);
					// 次画面のアクティビティ起動
					startActivity(intent);
					finish();
				} else {
					// インテントのインスタンス生成
					intent = new Intent(TutorialActivity.this,
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
				intent = new Intent(TutorialActivity.this,
						StartUpActivity.class);
				// 次画面のアクティビティ起動
				startActivity(intent);
				finish();
			} else {
				// インテントのインスタンス生成
				intent = new Intent(TutorialActivity.this,
						ReadyActivity.class);
				// 次画面のアクティビティ起動
				startActivity(intent);
				finish();
			}
			return true;
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
}