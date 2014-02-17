package com.alborgis.ting.mainapp.common.custom_controls.endless_horizontal_tile_gridview;

import com.alborgis.ting.mainapp.common.custom_controls.two_way_gridview.TwoWayAbsListView;
import com.alborgis.ting.mainapp.common.custom_controls.two_way_gridview.TwoWayGridView;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ArrayAdapter;

public class EndlessHorizontalTileGridView extends TwoWayGridView implements TwoWayGridView.OnScrollListener {

	View loadingView;
	private boolean isLoading;
	private EndlessGridViewListener listener;

	
	public static interface EndlessGridViewListener {
		public void onGridViewRequestLoadData();
	}
	

	public EndlessHorizontalTileGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.setOnScrollListener(this);
	}

	public EndlessHorizontalTileGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setOnScrollListener(this);
	}

	public EndlessHorizontalTileGridView(Context context) {
		super(context);
		this.setOnScrollListener(this);
	}

	public void setListener(EndlessGridViewListener listener) {
		this.listener = listener;
	}

	@Override
	public void onScrollStateChanged(TwoWayAbsListView view, int scrollState) {
		
	}

	@Override
	public void onScroll(TwoWayAbsListView view, int firstVisibleItem,	int visibleItemCount, int totalItemCount) {
		if (getAdapter() == null)
			return;

		if (getAdapter().getCount() == 0)
			return;

		int l = visibleItemCount + firstVisibleItem;
		if (l >= totalItemCount && !isLoading) {
				// It is time to add new data. We call the listener
				isLoading = true;
				listener.onGridViewRequestLoadData();
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

	public EndlessGridViewListener setListener() {
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
