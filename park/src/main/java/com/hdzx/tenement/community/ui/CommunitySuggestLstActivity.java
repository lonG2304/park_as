package com.hdzx.tenement.community.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.*;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hdzx.tenement.R;
import com.hdzx.tenement.common.UserSession;
import com.hdzx.tenement.common.vo.MediaDataHolder;
import com.hdzx.tenement.community.adapter.TxtSimpleAdapter;
import com.hdzx.tenement.community.vo.ExtraInfo;
import com.hdzx.tenement.community.vo.Suggest;
import com.hdzx.tenement.http.protocol.*;
import com.hdzx.tenement.mine.adaper.MyMessagesAdapter;
import com.hdzx.tenement.mine.ui.MinePostsDtlActivity;
import com.hdzx.tenement.mine.vo.Notice;
import com.hdzx.tenement.photo.Configs;
import com.hdzx.tenement.photo.FileTools;
import com.hdzx.tenement.photo.SelectHeadTools;
import com.hdzx.tenement.pulltorefresh.library.ILoadingLayout;
import com.hdzx.tenement.pulltorefresh.library.PullToRefreshBase;
import com.hdzx.tenement.pulltorefresh.library.PullToRefreshListView;
import com.hdzx.tenement.ui.common.RecordActivity;
import com.hdzx.tenement.utils.AESUtils;
import com.hdzx.tenement.utils.CloseKeyBoard;
import com.hdzx.tenement.utils.Contants;
import com.hdzx.tenement.widget.FixGridLayout;
import com.loopj.android.http.RequestParams;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CommunitySuggestLstActivity extends Activity implements
		IContentReportor {

	//
	private List<Suggest> bbsPostsVoList;
	private TxtSimpleAdapter myPostsAdapter;
	private com.hdzx.tenement.pulltorefresh.library.PullToRefreshListView lst_my_posts;
	private String RQ_MY_POSTS = "rq_my_posts";
	private HttpAsyncTask task = null;
	private final int PAGE_SIZE = 20;
	private int pageIndex = 1;
	private ImageView back_imageView;
	private TextView tv_title;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.community_suggest_lst);
		lst_my_posts = (PullToRefreshListView) findViewById(R.id.lst_my_posts);
		back_imageView = (ImageView) findViewById(R.id.back_imageView);
		tv_title = (TextView) findViewById(R.id.titile_tv);
		tv_title.setText("历史记录");
		
		initData();

	}

	private void initData() {
		// TODO Auto-generated method stub
		initIndicator();
		bbsPostsVoList = new ArrayList<Suggest>();
		
		myPostsAdapter = new TxtSimpleAdapter(this, bbsPostsVoList);
		lst_my_posts.setAdapter(myPostsAdapter);

		lst_my_posts
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						
//						Log.v("gl", position+"getThreadId==="+((BbsPosts)parent.getAdapter().getItem(position-1)).getContent());
						Intent intent = new Intent(parent.getContext(),
								CommunitySuggestDtlActivity.class);
						intent.putExtra("adviceId",bbsPostsVoList.get(position-1).getAdviceId());
						startActivityForResult(intent, 0);
					}
				});

		lst_my_posts
				.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						
						pageIndex = 1;
						bbsPostsVoList.clear();
		                getData(pageIndex);
						
						
					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						
						getData(++pageIndex);
						
					}
				});

		getData(pageIndex);
	}

	/**
	 * 初始化数据
	 */
	private void getData(int pageIndex) {
		// HEARD
		RequestContentTemplate reqContent = new RequestContentTemplate();
		reqContent.setEncryptoType(Contants.CryptoTyepEnum.aes);// 请求使用AES加密

		// BODY
		reqContent.setRequestTicket(true);
		reqContent.appendData(Contants.PROTOCOL_REQ_BODY_DATA.operType.name(),
				"000");
		reqContent.appendData(Contants.PROTOCOL_REQ_BODY_DATA.pageNum.name(),
				pageIndex + "");
		reqContent.appendData(Contants.PROTOCOL_REQ_BODY_DATA.pageSize.name(),
				PAGE_SIZE + "");

		// SEND
		HttpRequestEntity httpRequestEntity = new HttpRequestEntity(reqContent,
				Contants.SERVER_HOST,
				Contants.PROTOCOL_COMMAND.GET_SUGGEST_LST.getValue());
		httpRequestEntity.setRequestCode(RQ_MY_POSTS);
		httpRequestEntity.setHasData(true);

		httpRequestEntity.setResponseDecryptoType(Contants.CryptoTyepEnum.aes);// 返回使用AES密钥解密
		task = new HttpAsyncTask(this, this);
		task.execute(httpRequestEntity);
	}

	

	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.back_iv:
			finish();
			break;
		default:
			break;
		}
	}

	@Override
	public void reportBackContent(ResponseContentTamplate responseContent) {
		// TODO Auto-generated method stub
		String rtnCode = (String) responseContent
				.getInMapHead(Contants.PROTOCOL_RESP_HEAD.rtnCode.name());
		if (rtnCode == null || "".equals(rtnCode)) {
			Toast.makeText(getApplicationContext(), "返回为空", Toast.LENGTH_SHORT)
					.show();
		} else if (!Contants.ResponseCode.CODE_000000.equals(rtnCode)) {
			Toast.makeText(getApplicationContext(),
					responseContent.getErrorMsg(), Toast.LENGTH_SHORT).show();
		} else {

			if (RQ_MY_POSTS.equals(responseContent.getResponseCode())) {
				String jsonStr = responseContent.getDataJson().trim();
				System.out.println("data=" + jsonStr);
				if (jsonStr != null && !"".equals(jsonStr)
						&& jsonStr instanceof String) {

					Gson gson = new Gson();
					List<Suggest> list = gson.fromJson(jsonStr,
							new TypeToken<List<Suggest>>() {
							}.getType());

					bbsPostsVoList.addAll(list);
				}
				showInfo();
			}
		}
	}

	private void showInfo() {
		// TODO Auto-generated method stub
		myPostsAdapter.notifyDataSetChanged();

		lst_my_posts.onRefreshComplete();
	}

	private void initIndicator() {
		ILoadingLayout loadingLayout = lst_my_posts.getLoadingLayoutProxy(true,
				false);
		loadingLayout.setPullLabel("下拉刷新");
		loadingLayout.setRefreshingLabel("加载中……");
		loadingLayout.setReleaseLabel("释放刷新当前画面");

		ILoadingLayout loadingLayout2 = lst_my_posts.getLoadingLayoutProxy(
				false, true);
		loadingLayout2.setPullLabel("上拉加载更多");
		loadingLayout2.setRefreshingLabel("加载中……");
		loadingLayout2.setReleaseLabel("释放加载更多");

	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		if(requestCode==0){
			pageIndex = 1;
			bbsPostsVoList.clear();
			getData(pageIndex);
			
			Log.v("gl", "onActivityResult");
		}
			
	}
}