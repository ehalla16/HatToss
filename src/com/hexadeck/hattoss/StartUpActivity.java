package com.hexadeck.hattoss;

import jp.basicinc.gamefeat.ranking.android.sdk.controller.GFRankingController;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;

public class StartUpActivity extends Activity implements OnClickListener {

	// --------------------------------------------------------------------------
	// ボタンが押された時の処理
	// --------------------------------------------------------------------------
	public void onClick(View v) {
		Intent intent = null;
		// 遷移先のActivityを指定して、Intentを作成する
		switch (v.getId()) {
		case R.id.button_agree:
			intent = new Intent(this, ReadyActivity.class);
			// 遷移先のアクティビティを起動させる
			startActivity(intent);
			finish();
			break;
		case R.id.button_left:
			intent = new Intent(this, TutorialActivity.class);
			intent.putExtra("agreement", 0);
			// 遷移先のアクティビティを起動させる
			startActivity(intent);
			break;
		case R.id.button_right:
			 intent = new Intent(this, RankingAllActivity.class);
			 intent.putExtra("agreement", 0 );
			 // 遷移先のアクティビティを起動させる
			 startActivity(intent);
			break;
		}
	}

	// --------------------------------------------------------------------------
	// ライフサイクル 1 : アクティビティ初期化（iOSの、viewDidLoad）
	// --------------------------------------------------------------------------
	public void onCreate(Bundle saved_instance_state_) {
		super.onCreate(saved_instance_state_);
		setContentView(R.layout.startup);
		Button btnMain = (Button) findViewById(R.id.button_agree);
		Button btnLeft = (Button) findViewById(R.id.button_left);
		Button btnRight = (Button) findViewById(R.id.button_right);
		btnMain.setOnClickListener(this);
		btnLeft.setOnClickListener(this);
		btnRight.setOnClickListener(this);
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
		finish();
		super.onDestroy();
	}
}