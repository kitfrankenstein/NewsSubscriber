package com.kit.newsSubscriber.news;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.Toast;


import com.example.newsSubscriber.R;

import com.kit.newsSubscriber.base.BaseFragment;
import com.kit.newsSubscriber.db.NewsCache;
import com.kit.newsSubscriber.notification.MqttManager;
import com.kit.newsSubscriber.notification.PreferencesManager;
import com.kit.newsSubscriber.util.ToastUtil;
import com.kit.newsSubscriber.util.SysInfoUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class FragmentSetting extends BaseFragment {

	private Context context;
	private NewsCache cache;
	private MqttManager mqttManager;
	private PreferencesManager preferencesManager;
	private Map<String, Boolean> preferences;
	private List<CheckBox> checkBoxes;
	private Switch aSwitch;
	private Button btnCache, btnHistory;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		context = getActivity();
		View fragmentView = inflater.inflate(R.layout.fragment_setting, container, false);

		cache = NewsCache.getInstance(context);
		preferencesManager = PreferencesManager.getInstance(context);
		mqttManager = MqttManager.getInstance(context);

		checkBoxes = new ArrayList<>();
		initCompoundButtons(fragmentView);

		btnCache = fragmentView.findViewById(R.id.btnCache);
		btnHistory = fragmentView.findViewById(R.id.btnHistory);
		initButtons();

		return fragmentView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	private void initButtons() {
		btnCache.setOnClickListener(v -> {
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			AlertDialog alertDialog = builder.setTitle("确定要清除缓存的内容吗?")
					.setPositiveButton("确定", (dialog, which) -> {
						dialog.dismiss();
						cache.clearCache();
						Toast.makeText(context, "缓存已清除", Toast.LENGTH_SHORT).show();
					})
					.setNegativeButton("取消", (dialog, which) -> dialog.cancel())
					.create();
			alertDialog.show();
			alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.RED);
			alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.BLUE);
		});
		btnHistory.setOnClickListener(v -> {
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			AlertDialog alertDialog = builder.setTitle("确定要清空历史记录吗?")
					.setPositiveButton("确定", (dialog, which) -> {
						dialog.dismiss();
						cache.clearHistory();
						Toast.makeText(context, "历史记录已全部删除", Toast.LENGTH_SHORT).show();
					})
					.setNegativeButton("取消", (dialog, which) -> dialog.cancel())
					.create();
			alertDialog.show();
			alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.RED);
			alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.BLUE);
		});
	}

	private void initCompoundButtons(View fragmentView) {
		aSwitch = fragmentView.findViewById(R.id.focus);

		checkBoxes.add(fragmentView.findViewById(R.id.rcmdBox));
		checkBoxes.add(fragmentView.findViewById(R.id.intlBox));
		checkBoxes.add(fragmentView.findViewById(R.id.finBox));
		checkBoxes.add(fragmentView.findViewById(R.id.milBox));
		checkBoxes.add(fragmentView.findViewById(R.id.techBox));
		checkBoxes.add(fragmentView.findViewById(R.id.sptBox));
		checkBoxes.add(fragmentView.findViewById(R.id.entBox));
		checkBoxes.add(fragmentView.findViewById(R.id.eduBox));
		checkBoxes.add(fragmentView.findViewById(R.id.gameBox));
		checkBoxes.add(fragmentView.findViewById(R.id.healBox));

		preferences = preferencesManager.getPreferences();

		boolean enable = preferences.get(aSwitch.getTag().toString());
		aSwitch.setOnCheckedChangeListener(switchChangeListener);
		aSwitch.setChecked(enable);

		for (CheckBox checkBox : checkBoxes) {
			checkBox.setOnCheckedChangeListener(checkedChangeListener);
			String key = checkBox.getTag().toString();
			checkBox.setChecked(preferences.get(key));
			checkBox.setEnabled(enable);
		}
	}

	private OnCheckedChangeListener checkedChangeListener = (buttonView, isChecked) -> {
		/* 处理选中状态改变事件，动态显示选择结果 */
		String key = buttonView.getTag().toString();
		preferencesManager.setPreference(key, isChecked);
		if (isChecked) {
			mqttManager.subscribe(key, 1);
		} else {
			mqttManager.unSubscribe(key);
		}
	};

	private OnCheckedChangeListener switchChangeListener = (buttonView, isChecked) -> {
		/* 处理选中状态改变事件，动态显示选择结果 */
		if (isChecked && (SysInfoUtil.isNotificationEnable(context))) {
			preferencesManager.setPreference(aSwitch.getTag().toString(), true);
			mqttManager.subscribe(aSwitch.getTag().toString(), 1);
			for (CheckBox checkBox : checkBoxes) {
				checkBox.setEnabled(true);
				if (checkBox.isChecked()) {
					mqttManager.subscribe(checkBox.getTag().toString(), 1);
				}
			}
		} else if (isChecked && !(SysInfoUtil.isNotificationEnable(context))) {
			buttonView.setChecked(false);
			ToastUtil.show(context, "请先在系统设置中打开通知开关");
			//Toast.makeText(context, "请先在系统设置中打开通知开关", Toast.LENGTH_SHORT).show();
		} else {
			System.out.println("aSwitch checked false ");
			preferencesManager.setPreference(aSwitch.getTag().toString(), false);
			mqttManager.unSubscribe(aSwitch.getTag().toString());
			for (CheckBox checkBox : checkBoxes) {
				if (checkBox.isChecked()) {
					mqttManager.unSubscribe(checkBox.getTag().toString());
				}
				checkBox.setEnabled(false);
			}
		}
	};

}