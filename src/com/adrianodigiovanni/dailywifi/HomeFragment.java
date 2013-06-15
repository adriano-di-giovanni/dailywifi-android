package com.adrianodigiovanni.dailywifi;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

public class HomeFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_home, container, false);
		
		WebView webView = (WebView) view.findViewById(R.id.web_view);
		webView.loadUrl("file:///android_asset/www/index.html");
		
		return view;
	}
}
