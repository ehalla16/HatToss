package com.hexadeck.hattoss;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.hexadeck.hattoss.camera.CameraPreviewView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Camera;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ReadyActivity extends Activity implements SensorEventListener {

	private ViewGroup mRootViewGroup;
	private SensorManager manager;
	private Button button;
	private double acceleX, acceleY, acceleZ;
	private double ms2; // 3軸合成値
	private Timer timer; // タイマー
	private double upSec, dropSec; // カウンター
	private double duration; // 滞空時間
	private BigDecimal mResult; // 計算結果
	private static String resultText; // 計算結果テキスト

	// カメラプレビュークラス
	private CameraPreviewView mCameraPreviewView;

	/** カメラが現在使用可能かどうか */
	private boolean mCameraAvailable = true;

	private static double THRESHOLD = 2d; // 最高点到達判定閾値
	private static double VALUE_G = 9.8d; // 地球の重力加速度

	// チェック用
	private TextView values;
	private double mMax, mMin;
	private static final String TAG = "ReadyActivity";

	AsyncTask<Void, Void, Void> task;

	public boolean measureFlag;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera_preview);

		Typeface face = Typeface.createFromAsset(getAssets(),
				"fonts/intro_inline.ttf");
		mCameraPreviewView = new CameraPreviewView(this);
		mRootViewGroup = (ViewGroup) findViewById(R.id.camera_frame);
		mRootViewGroup.addView(mCameraPreviewView);
		View view = this.getLayoutInflater().inflate(R.layout.activity_main,
				null);
		addContentView(view, new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT));

//		values = (TextView) findViewById(R.id.values);
		manager = (SensorManager) getSystemService(SENSOR_SERVICE);
		button = (Button) findViewById(R.id.button_ready);
		button.setTypeface(face);
		Button btnLeft = (Button) findViewById(R.id.tutorial_button);
		Button btnRight = (Button) findViewById(R.id.ranking_button);
		button.setOnTouchListener(m_touchListener);
		btnLeft.setOnClickListener(m_clickListener);
		btnRight.setOnClickListener(m_clickListener);

	}

	/** タッチイベント処理 */
	private OnTouchListener m_touchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_UP) {
				Log.d(TAG, "ACTION_UP");
				measure();
			} else {
				Log.d(TAG, "ACTION_DOWN");
				button.setText("Let's TOSS!!");
			}
			return true;
		}
	};

	// --------------------------------------------------------------------------
	// ボタンが押された時の処理
	// --------------------------------------------------------------------------
	private OnClickListener m_clickListener = new OnClickListener() {

		public void onClick(View v) {
			Intent intent = null;
			// 遷移先のActivityを指定して、Intentを作成する
			switch (v.getId()) {
			case R.id.tutorial_button:
				intent = new Intent(ReadyActivity.this, TutorialActivity.class);
				// 遷移先のアクティビティを起動させる
				startActivity(intent);
				finish();
				break;
			case R.id.ranking_button:
				intent = new Intent(ReadyActivity.this, RankingAllActivity.class);
				// 遷移先のアクティビティを起動させる
				startActivity(intent);
				finish();
				break;
			}
		}
	};

	/** 投げた後の処理 */
	public BigDecimal measure() {
		Log.d(TAG, "measure");
		// メソッド重複実行防止
		if (measureFlag == true) {
			task = new AsyncTask<Void, Void, Void>() {
				@Override
				protected void onPreExecute() {
					button.setText("YEAHHHH!!");
					Log.d(TAG, "投擲");
					if (ms2 < 10) {
						Intent intent = new Intent(ReadyActivity.this,
								ReadyActivity.class);
						intent.putExtra("THROWN", false);
						// 次画面のアクティビティ起動
						finish();
						startActivity(intent);
						task.cancel(true);
						Log.d(TAG, "投擲失敗");
					}
					measureFlag = false;
				}

				@Override
				protected Void doInBackground(Void... params) {
					if (isCancelled()) {
						Log.d(TAG, "Cancelled!");
						task.cancel(true);
					}
					timer = new Timer();
					Counter uc = new Counter();
					// 0.01秒ごとに実行
					timer.scheduleAtFixedRate(uc, 0, 1);
					while (ms2 > VALUE_G) {
						Log.d("ms2", " = " + ms2);
						Log.d("mMax", " = " + mMax);
						Log.d("mMin", " = " + mMin);
						Log.d("sec", " = " + uc.getSec());
						Log.d("measure", "上昇中");
					}
					Log.d("measure", "最高点到達");
					timer.cancel();
					upSec = uc.getSec();
					Log.d("measure", "上昇時間 = " + upSec);
					return null;
				}

				@Override
				protected void onPostExecute(Void result) {
					Log.d(TAG, "onPostExecute");
					// シャッター切る
					if (mCameraAvailable) { // カメラスタンバイ確認
						mCameraPreviewView.mCamera.takePicture(null, null,
								null, kPictureCallback);
						Log.d("mCameraAvailable", "撮影");
						mCameraAvailable = false; // 画像生成中　カメラスタンバイNG
					}
					// 距離 = (9.8 * 時間の2乗) / 2
					double raw = VALUE_G * (Math.pow(upSec, 2)) / 2;
					BigDecimal bd = new BigDecimal(raw);
					// 小数点2位以下四捨五入
					mResult = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
					Log.d("mResult", " = " + mResult);
				}
			};
			task.execute(); // ここでは何も渡さない
		}
		Log.d(TAG, "measureFlag = " + measureFlag);
		return mResult;
	}

	/** 滞空時間測定 */
	public double duration() {
		timer = new Timer();
		Counter c = new Counter();
		// 0.001秒ごとに実行
		timer.scheduleAtFixedRate(c, 0, 1);
		if (ms2 < THRESHOLD) {
			timer.cancel();
			duration = c.getSec();
		}
		return duration;
	}

	/**
	 * カメラで撮った写真を正しい向きにして返す。 返される画像は変更可能となる。
	 * 
	 * @param cameraBitmap
	 *            カメラで撮った正しい向きでないBitmap
	 * @param degrees
	 *            この写真を取ったときの画面の向き。0、90、180、270のいずれか。
	 * @return 正しい向きのミュータブルなBitmap
	 */
	private static Bitmap getMutableRotatedCameraBitmap(Bitmap cameraBitmap,
			int degrees) {
		int width, height;
		if (degrees % 180 == 0) { // 画面が横向きなら
			width = cameraBitmap.getWidth();
			height = cameraBitmap.getHeight();
		} else {
			width = cameraBitmap.getHeight();
			height = cameraBitmap.getWidth();
		}
		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		canvas.rotate(degrees, width / 2, height / 2);

		int offset = (degrees % 180 == 0) ? 0
				: (degrees == 90) ? (width - height) / 2 : (height - width) / 2;

		canvas.translate(offset, -offset);
		canvas.drawBitmap(cameraBitmap, 0, 0, null);
		cameraBitmap.recycle();
		return bitmap;
	}

	/**
	 * カメラで撮った写真を正しい向きにして返す。 返される画像は変更不可能となる。
	 * 
	 * @param cameraBitmap
	 *            カメラで撮った正しい向きでないBitmap
	 * @param degrees
	 *            この写真を取ったときの画面の向き。0、90、180、270のいずれか。
	 * @return 正しい向きのイミュータブルなBitmap
	 */
	@SuppressWarnings("unused")
	private static Bitmap getImmutableRotatedCameraBitmap(Bitmap cameraBitmap,
			int degrees) {
		Matrix m = new Matrix();
		m.postRotate(degrees);
		return Bitmap.createBitmap(cameraBitmap, 0, 0, cameraBitmap.getWidth(),
				cameraBitmap.getHeight(), m, false);
	}

	/**
	 * 写真を撮ったときに呼ばれるコールバック関数
	 */
	private final Camera.PictureCallback kPictureCallback = new Camera.PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {

			Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
			int degrees = CameraPreviewView
					.getCameraDisplayOrientation(ReadyActivity.this);

			// 回転された画像を得る。
			Bitmap rotatedBitmap = getMutableRotatedCameraBitmap(bitmap,
					degrees);
			// Bitmap rotatedBitmap = getImmutableRotatedCameraBitmap(bitmap,
			// degrees);
			showPicture(rotatedBitmap);
		}
	};

	/**
	 * 撮影した写真を画面に表示する。 保存用のロジックもここに記述している。
	 */
	private void showPicture(final Bitmap bitmap) {

		// Inflaterを使ってxmlのレイアウトを画面上に貼り付ける
		final View pictureView = getLayoutInflater().inflate(R.layout.picture,
				null);
		addContentView(pictureView, new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT));

		resultText = (measure()).toString();
		Typeface face = Typeface.createFromAsset(getAssets(),
				"fonts/intro_inline.ttf");

		// 画像にリザルトを書き込む
		Canvas canvas = new Canvas(bitmap);
		Paint paint = new Paint();
		paint.setTypeface(face);
		paint.setColor(Color.rgb(255,140,0));
		paint.setTextSize(30f);
		paint.setAntiAlias(true);
		paint.setTextAlign(Paint.Align.CENTER);
		Paint paint2 = new Paint();
		paint2.setTypeface(face);
		paint2.setColor(Color.rgb(255,140,0));
		paint2.setTextSize(90f);
		paint2.setAntiAlias(true);
		paint2.setTextAlign(Paint.Align.CENTER);
		canvas.drawText("RESULT", canvas.getWidth() / 2, 150, paint);
		canvas.drawText(resultText + "m", canvas.getWidth() / 2, 350, paint2);

		ImageView iv = (ImageView) findViewById(R.id.picture_view);
		iv.setImageBitmap(bitmap);

		button.setText("Hold here!!");
		final Button retryButton = (Button) findViewById(R.id.retry_button);
		final Button tweetButton = (Button) findViewById(R.id.tweet_button);

		View.OnClickListener listener = new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (v.equals(tweetButton)) {
					try {
						// ローカルファイルへ保存
						final FileOutputStream out = openFileOutput(
								"hatTossImgTmp.jpg",
								Context.MODE_WORLD_READABLE);
						bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
						out.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					// インテントのインスタンス生成
					Intent intent = new Intent(ReadyActivity.this,
							ResultActivity.class);
					// 次画面のアクティビティ起動
					startActivity(intent);
					finish();

				} else if (v.equals(retryButton)) {
					Intent intent = new Intent(ReadyActivity.this,
							ReadyActivity.class);
					// 次画面のアクティビティ起動
					finish();
					startActivity(intent);
				}

			}
		};
		iv.setOnClickListener(listener);
		retryButton.setOnClickListener(listener);
		tweetButton.setOnClickListener(listener);
	}

	public static String getResultText() {
		return resultText;
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.d(TAG, "onStop");
		// Listenerの登録解除
		manager.unregisterListener(this);
		// task.cancel(true);
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "onResume");
		// Listenerの登録
		List<Sensor> sensors = manager.getSensorList(Sensor.TYPE_ACCELEROMETER);
		if (sensors.size() > 0) {
			Sensor s = sensors.get(0);
			manager.registerListener(this, s, SensorManager.SENSOR_DELAY_UI);
		}

		mMax = VALUE_G;
		mMin = VALUE_G;

		// 計算メソッド重複防止用フラグ初期化
		measureFlag = true;

		// 投擲失敗時トースト表示
		Intent i = getIntent();
		boolean thrown = i.getBooleanExtra("THROWN", true);
		if (thrown == false) {
			Toast.makeText(getApplicationContext(), "TOSS Failed...",
					Toast.LENGTH_SHORT).show();
		}
		// 投擲フラグ初期化
		thrown = true;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// onAccuracyChanged メソッドは基本的に何もせず、呼び出されるたびにログ・エントリーを追加するだけです。
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		String str = "";
		acceleX = event.values[0];
		acceleY = event.values[1];
		acceleZ = event.values[2];
		ms2 = Math.sqrt((Math.pow(acceleX, 2)) + (Math.pow(acceleY, 2))
				+ (Math.pow(acceleZ, 2)));

		if (ms2 > mMax) {
			mMax = ms2;
		}
		if (ms2 < mMin) {
			mMin = ms2;
		}

		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			str = "X軸:" + acceleX + "\nY軸:" + acceleY + "\nZ軸:" + acceleZ
					+ "\n3軸合成値:" + ms2 + "\n3軸合成最大値:" + mMax + "\n3軸合成最小値:"
					+ mMin + "\nupSec:" + upSec + "\nmResult:" + mResult;
			//values.setText(str);
		}
	}

	// --------------------------------------------------------------------------
	// BACKキーが押された時の処理
	// --------------------------------------------------------------------------
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			new AlertDialog.Builder(this)
					.setTitle("アプリケーションの終了")
					.setMessage("アプリケーションを終了してよろしいですか？")
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									ReadyActivity.this.finish();
								}
							})
					.setNegativeButton("No",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// NOP
								}
							}).show();
			return true;
		}
		return false;
	}

}

class Counter extends TimerTask {
	private double sec;
	private volatile int counter = 0;

	public void run() {
		counter++;
		// Log.d("counter = ", Integer.toString(counter));
		double d = counter;
		setSec(d / 1000);
	}

	public double getSec() {
		return sec;
	}

	public void setSec(double sec) {
		this.sec = sec;
	}
}