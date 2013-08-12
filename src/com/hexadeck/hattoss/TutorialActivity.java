package com.hexadeck.hattoss;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
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
	// ボタンが押された時の処理
	// --------------------------------------------------------------------------
	public void onClick(View v_) {
		// アクティビティを終了させる事により、一つ前のアクティビティへ戻る事が出来る。
		finish();
	}

	// --------------------------------------------------------------------------
	// ライフサイクル 1 : アクティビティ初期化（iOSの、viewDidLoad）
	// --------------------------------------------------------------------------
	public void onCreate(Bundle saved_instance_state_) {
		super.onCreate(saved_instance_state_);

		// ボタンのインスタンスを作成する
		Button button = new Button(this);

		// ボタンにクリックイベントの通知先を設定する
		button.setOnClickListener(this);

		// ボタンにテキストを表示する
		button.setText("戻る");

		// レイアウトを作成する
		LinearLayout linear_layout = new LinearLayout(this);

		// レイアウトを画面に描画する
		setContentView(linear_layout);

		// レイアウト内要素の設定を作成する
		LinearLayout.LayoutParams layout_params = new LinearLayout.LayoutParams(
				MP, 80);
		layout_params.gravity = Gravity.CENTER_VERTICAL;

		// ボタンをレイアウトに配置する
		linear_layout.addView(button, layout_params);
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
	// BACKキーが押された時の処理
	// --------------------------------------------------------------------------
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;
		}
		return false;
	}
}