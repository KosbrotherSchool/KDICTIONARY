package com.kosbrother.kdictionary;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class QueryToolService extends Service
{

	private WindowManager windowManager;
	private ImageView chatHead;
	private RelativeLayout dialog_translateLayout;
	private Button buttonLang1;
	private Button buttonLang2;
	private ImageButton buttonTranslate;
	private ImageButton searchButton;
	private EditText searchEditText;
	private String translateString;
	private LinearLayout resultLayout;
	private final WindowManager.LayoutParams dialogParams = new WindowManager.LayoutParams(
			WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
			WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
			PixelFormat.TRANSLUCENT);
	private String[] langStrings;

	@Override
	public IBinder onBind(Intent intent)
	{
		// Not used
		return null;
	}

	@Override
	public void onCreate()
	{
		super.onCreate();

		windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

		chatHead = new ImageView(this);
		chatHead.setImageResource(R.drawable.ic_launcher);
		dialog_translateLayout = (RelativeLayout) LayoutInflater.from(getApplicationContext()).inflate(
				R.layout.dialog_translate, null);
		buttonLang1 = (Button) dialog_translateLayout.findViewById(R.id.button_lan_1);
		buttonLang2 = (Button) dialog_translateLayout.findViewById(R.id.button_lan_2);
		buttonTranslate = (ImageButton) dialog_translateLayout.findViewById(R.id.button_translate);
		searchEditText = (EditText) dialog_translateLayout.findViewById(R.id.edit_text_search);
		searchButton = (ImageButton) dialog_translateLayout.findViewById(R.id.button_search);
		resultLayout = (LinearLayout) dialog_translateLayout.findViewById(R.id.result_layout);
		
		langStrings = getResources().getStringArray(R.array.langs);
		buttonLang1.setText(langStrings[0]);
		buttonLang2.setText(langStrings[1]);
		
		final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);

		params.gravity = Gravity.TOP | Gravity.LEFT;
		params.x = 0;
		params.y = 100;

		dialogParams.gravity = Gravity.BOTTOM;
		windowManager.addView(dialog_translateLayout, dialogParams);

		// windowManager.addView(chatHead, params);

		chatHead.setOnTouchListener(new View.OnTouchListener()
		{
			private int initialX;
			private int initialY;
			private float initialTouchX;
			private float initialTouchY;

			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				switch (event.getAction())
				{
				case MotionEvent.ACTION_DOWN:
					initialX = params.x;
					initialY = params.y;
					initialTouchX = event.getRawX();
					initialTouchY = event.getRawY();
					return false;
				case MotionEvent.ACTION_UP:
					return false;
				case MotionEvent.ACTION_MOVE:
					params.x = initialX + (int) (event.getRawX() - initialTouchX);
					params.y = initialY + (int) (event.getRawY() - initialTouchY);
					windowManager.updateViewLayout(chatHead, params);
					return true;
				}
				return false;
			}

		});

		chatHead.setOnLongClickListener(new View.OnLongClickListener()
		{

			@Override
			public boolean onLongClick(View v)
			{
				final WindowManager.LayoutParams dialogParams = new WindowManager.LayoutParams(
						WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
						WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
						PixelFormat.TRANSLUCENT);
				dialogParams.gravity = Gravity.BOTTOM;
				windowManager.addView(dialog_translateLayout, dialogParams);

				return false;
			}

		});

		dialog_translateLayout.setOnTouchListener(new View.OnTouchListener()
		{

			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				// TODO Auto-generated method stub
				return false;
			}
		});

		buttonLang1.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				Toast.makeText(QueryToolService.this, "test toast", Toast.LENGTH_SHORT).show();
			}
		});

		buttonLang2.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				Toast.makeText(QueryToolService.this, "test toast 2", Toast.LENGTH_SHORT).show();
			}
		});

		buttonTranslate.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				Toast.makeText(QueryToolService.this, "轉換", Toast.LENGTH_SHORT).show();
			}
		});

		searchButton.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				Toast.makeText(QueryToolService.this, "搜索中...", Toast.LENGTH_SHORT).show();
				InputMethodManager inputManager = (InputMethodManager) getApplicationContext()
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				inputManager.hideSoftInputFromWindow(searchEditText.getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);

				new TranslateTask().execute();
			}
		});

	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		if (chatHead != null)
			windowManager.removeView(chatHead);
	}

	protected class TranslateTask extends AsyncTask<Void, Void, Void>
	{

		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();

		}

		@Override
		protected Void doInBackground(Void... Void)
		{
			if (searchEditText.getText().toString() != null && !searchEditText.getText().toString().equals(""))
			{
				String pharse = searchEditText.getText().toString();
				translateString = TranslateApi.getTransLate("eng", "zho", pharse);
			} else
			{
				translateString = "No Data!";
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result)
		{
			resultLayout.removeAllViews();
			TextView textView = new TextView(getApplicationContext());
			textView.setText(translateString);
			resultLayout.addView(textView);
			windowManager.updateViewLayout(dialog_translateLayout, dialogParams);
		}

	}
}