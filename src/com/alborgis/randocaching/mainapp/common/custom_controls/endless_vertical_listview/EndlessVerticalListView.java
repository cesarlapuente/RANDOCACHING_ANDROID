package com.alborgis.randocaching.mainapp.common.custom_controls.endless_vertical_listview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

public class EndlessVerticalListView extends ListView implements OnScrollListener {

	View loadingView;
	private boolean isLoading;
	private EndlessListViewListener listener;

	
	public static interface EndlessListViewListener {
		public void onListViewRequestLoadData();
	}

	
	public EndlessVerticalListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.setOnScrollListener(this);
	}

	public EndlessVerticalListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setOnScrollListener(this);
	}
	
    public EndlessVerticalListView(Context context) {
		super(context);
		this.setOnScrollListener(this);
	}
    
    
    public void setListener(EndlessListViewListener listener) {
		this.listener = listener;
	}
    
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    	
    }
    

	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if (getAdapter() == null)
			return;

		if (getAdapter().getCount() == 0)
			return;

		int l = visibleItemCount + firstVisibleItem;
		if (l >= totalItemCount && !isLoading) {
				// It is time to add new data. We call the listener
				isLoading = true;
				listener.onListViewRequestLoadData();
				showFooterView();
		}
    }
	
	
	public void setLoadingView(View view) {
		loadingView = view;
		this.hideFooterView();
	}
	
	public void setAdapter(ArrayAdapter<?> adapter) {
		super.setAdapter(adapter);
		this.isLoading = false;
		this.hideFooterView();
	}

	public EndlessListViewListener setListener() {
		return listener;
	}

	
	public void showFooterView(){
		if(loadingView != null){
			loadingView.setVisibility(View.VISIBLE);
		}
	}
	
	public void hideFooterView(){
		if(loadingView != null){
			loadingView.setVisibility(View.GONE);
		}
	}
	
	public boolean isLoading() {
		return isLoading;
	}

	public void setLoading(boolean isLoading) {
		this.isLoading = isLoading;
	}
 
    
 
}
