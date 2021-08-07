package com.hoperun.control.slice;

import com.hoperun.control.ResourceTable;
import com.hoperun.control.component.SelectDeviceDialog;
import com.hoperun.control.constants.EventConstants;
import com.hoperun.control.proxy.ConnectManagerIml;
import com.hoperun.control.utils.AbilityMgr;
import com.hoperun.control.utils.LogUtils;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Component;
import ohos.agp.components.Text;
import ohos.agp.window.dialog.ToastDialog;
import ohos.event.commonevent.*;
import ohos.rpc.RemoteException;

public class MainAbilitySlice extends AbilitySlice {
    private static final String ABILITY_NAME = "com.hoperun.control.RemoteControlAbility";
    private static final String TAG = MainAbilitySlice.class.getSimpleName();
    private static final String MOVIE_URL = "entry/resources/base/media/gubeishuizhen.mp4";

    private Text selectDeviceText;
    private Text receiveText;
    private AbilityMgr abilityMgr = new AbilityMgr(this);
    private MyCommonEventSubscriber subscriber;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main);

        initViews();
        subscribe();
    }

    private void initViews() {
        selectDeviceText = (Text) findComponentById(ResourceTable.Id_top);
        selectDeviceText.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                showDevicesDialog();
            }
        });

        receiveText = (Text) findComponentById(ResourceTable.Id_receive_input);
    }


    private void showDevicesDialog() {
        new SelectDeviceDialog(getContext(), deviceInfo -> {
            abilityMgr.openRemoteAbility(deviceInfo.getDeviceId(), getBundleName(), ABILITY_NAME);
            selectDeviceText.setText(deviceInfo.getDeviceName());
        }).show();
    }

    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }

    private void subscribe() {
        MatchingSkills matchingSkills = new MatchingSkills();
        matchingSkills.addEvent(EventConstants.SCREEN_REMOTE_CONTROLL_EVENT);
        matchingSkills.addEvent(CommonEventSupport.COMMON_EVENT_SCREEN_ON);
        CommonEventSubscribeInfo subscribeInfo = new CommonEventSubscribeInfo(matchingSkills);
        subscriber = new MyCommonEventSubscriber(subscribeInfo);
        try {
            CommonEventManager.subscribeCommonEvent(subscriber);
        } catch (RemoteException e) {
            LogUtils.error("", "subscribeCommonEvent occur exception.");
        }
    }

    private void unSubscribe() {
        try {
            CommonEventManager.unsubscribeCommonEvent(subscriber);
        } catch (RemoteException e) {
            LogUtils.error(TAG, "unSubscribe Exception");
        }
    }

    /**
     * 公共事件订阅处理
     *
     * @since 2020-12-03
     */
    class MyCommonEventSubscriber extends CommonEventSubscriber {
        MyCommonEventSubscriber(CommonEventSubscribeInfo info) {
            super(info);
        }

        @Override
        public void onReceiveEvent(CommonEventData commonEventData) {
            Intent intent = commonEventData.getIntent();
            int requestType = intent.getIntParam("requestType", 0);
            if (requestType == ConnectManagerIml.REQUEST_SEND_DATA) {
                String inputString = intent.getStringParam("inputString");
                receiveText.setText(inputString);
            } else if (requestType == ConnectManagerIml.REQUEST_PLUS) {
                String plusResult = intent.getStringParam("plusResult");
                new ToastDialog(MainAbilitySlice.this).setText("计算成功:" + plusResult + " 回传数据").show();
            } else {

            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        unSubscribe();
    }
}
