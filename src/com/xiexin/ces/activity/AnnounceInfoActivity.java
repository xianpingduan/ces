package com.xiexin.ces.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.xiexin.ces.App;
import com.xiexin.ces.R;

public class AnnounceInfoActivity extends Activity implements OnClickListener {

	private final static String TAG = "AnnounceInfoActivity";

	private String mAnnounceContent;
	private String mAnnounceId;
	private String mAnnounceTitle;

	// header start
	private LinearLayout mReturnLl;
	private ImageView mReturnIv;
	private TextView mReturnTv;
	private TextView mTitle;
	private Button mBtn1;
	private Button mBtn2;

	// header end

	private TextView mAnnounceTitleTv;
	private TextView mAnnounceContentTv;

	private RequestQueue mQueue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_announce);
		mQueue = Volley.newRequestQueue(App.getAppContext());
		initView();

		initData();
	}

	private void initView() {
		// header start
		mReturnLl = (LinearLayout) findViewById(R.id.return_ll);
		mReturnIv = (ImageView) findViewById(R.id.return_iv);
		mReturnTv = (TextView) findViewById(R.id.return_tv);
		mTitle = (TextView) findViewById(R.id.title);
		mBtn1 = (Button) findViewById(R.id.btn1);
		mBtn2 = (Button) findViewById(R.id.btn2);
		// /header end
		mReturnIv.setVisibility(View.VISIBLE);

		mTitle.setText(getString(R.string.announce_center));
		mBtn1.setVisibility(View.GONE);
		mReturnLl.setOnClickListener(this);

		mAnnounceTitleTv = (TextView) findViewById(R.id.announce_title_tv);
		mAnnounceContentTv = (TextView) findViewById(R.id.announce_content_tv);

	}

	private void initData() {
		Intent intent = getIntent();
		mAnnounceContent = intent.getStringExtra("content");
		mAnnounceId = intent.getStringExtra("id");
		mAnnounceTitle = intent.getStringExtra("title");
		mAnnounceTitleTv.setText(mAnnounceTitle);
		mAnnounceContentTv.setText(Html.fromHtml(mAnnounceContent));

		//doMsgRead( );

	}

	// private void doMsgRead()
	// {
	// StringBuffer urlSbf = new StringBuffer( App.getRootUrl( ) +
	// Constants.SET_MESSAGE_READ + "?" );
	//
	// String account = App.getSharedPreference( ).getString(
	// Constants.ZHANG_TAO_CONN_NAME , "" );
	// String userid = App.getSharedPreference( ).getString( Constants.USER_ID ,
	// "" );
	// urlSbf.append( "account=" ).append( account );
	// urlSbf.append( "&userid=" ).append( userid );
	// urlSbf.append( "&id=" ).append( mAnnounceId );
	// Logger.d( TAG , "urlSbf=" + urlSbf.toString( ) );
	// JsonObjectRequest json = new JsonObjectRequest( Method.GET ,
	// urlSbf.toString( ) , null , new Listener< JSONObject >( )
	// {
	// @Override
	// public void onResponse( JSONObject response )
	// {
	// try
	// {
	// int resCode = response.getInt( "success" );
	// if( resCode == 0 )
	// {
	// Logger.d( TAG , response.getString( "data" ) );
	// }
	// else
	// {
	// Logger.d( TAG , response.getString( "msg" ) );
	// }
	// }
	// catch ( JSONException e )
	// {
	// e.printStackTrace( );
	// }
	// }
	// } , new ErrorListener( )
	// {
	// @Override
	// public void onErrorResponse( VolleyError error )
	// {
	// Logger.d( TAG , "onErrorResponse:" + error.getMessage( ) );
	// }
	// } );
	// mQueue.add( json );
	// mQueue.start( );
	// }

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.return_ll:
			onBackPressed();
			break;
		default:
			break;
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		setResult();
	}

	private void setResult() {
		Intent in = new Intent();
		setResult(RESULT_OK, in);
		finish();
	}
}