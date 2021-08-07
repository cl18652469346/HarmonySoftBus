package com.hoperun.control.slice;

import com.hoperun.control.ResourceTable;
import com.hoperun.control.constants.EventConstants;
import com.hoperun.control.proxy.ConnectManager;
import com.hoperun.control.proxy.ConnectManagerIml;
import com.hoperun.control.utils.AbilityMgr;
import com.hoperun.control.utils.LogUtils;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.ability.IAbilityContinuation;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.IntentParams;
import ohos.agp.components.*;
import ohos.agp.components.surfaceprovider.SurfaceProvider;
import ohos.agp.graphics.Surface;
import ohos.agp.graphics.SurfaceOps;
import ohos.agp.utils.LayoutAlignment;
import ohos.agp.window.dialog.ToastDialog;
import ohos.agp.window.service.WindowManager;
import ohos.event.commonevent.*;
import ohos.global.resource.RawFileDescriptor;
import ohos.media.common.Source;
import ohos.media.player.Player;
import ohos.rpc.RemoteException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RemoteControlAbilitySlice extends AbilitySlice implements IAbilityContinuation {
    private static final String TAG = MainAbilitySlice.class.getName();

    private static final int SHOW_KEYBOARD_DELAY = 800;
    private static final int INIT_SIZE = 8;
    private String deviceIdConn;
    private ConnectManager connectManager;
    private TextField textField;

    private TextField plusA, plusB;
    private Button plusBtn;
    private Text plusText;

    private Button migrateBtn, migrateBackBtn;
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_remote_control);


        initView();
        initListener();
        // 延时 800ms 模拟点击事件，弹出软键盘
        showKeyBoard();

        // 获取想被控制的设备ID
        deviceIdConn = intent.getStringParam("localDeviceId");
        // 连接被控制的设备PA
        initConnManager(deviceIdConn);
    }

    private void initView() {
        if (findComponentById(ResourceTable.Id_remote_input) instanceof TextField) {
            textField = (TextField) findComponentById(ResourceTable.Id_remote_input);
            textField.requestFocus();
        }
        plusA = (TextField) findComponentById(ResourceTable.Id_plus_a);
        plusB = (TextField) findComponentById(ResourceTable.Id_plus_b);
        plusText = (Text) findComponentById(ResourceTable.Id_result_text);
        plusBtn = (Button) findComponentById(ResourceTable.Id_plus);
        migrateBtn = (Button) findComponentById(ResourceTable.Id_migrate);
        migrateBackBtn = (Button) findComponentById(ResourceTable.Id_migrate_back);
    }

    private void initListener() {
        // 监听文本变化，远程显示
        textField.addTextObserver((ss, ii, i1, i2) -> {
            Map<String, String> map = new HashMap<>(INIT_SIZE);
            map.put("inputString", ss);
            if (connectManager != null) {
                connectManager.sendRequest(ConnectManagerIml.REQUEST_SEND_DATA, map);
            }
        });

        plusBtn.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                Map<String, String> map = new HashMap<>(INIT_SIZE);
                if (plusA.getText().isEmpty() || plusB.getText().isEmpty()) {
                    new ToastDialog(RemoteControlAbilitySlice.this).setAlignment(LayoutAlignment.CENTER).setText("请输入数字").show();
                    return;
                }
                map.put("plusA", plusA.getText());
                map.put("plusB", plusB.getText());
                if (connectManager != null) {
                    int result = connectManager.sendRequest(ConnectManagerIml.REQUEST_PLUS, map);
                    new ToastDialog(RemoteControlAbilitySlice.this).setText("计算结果接收成功").show();
                    plusText.setText("" + result);
                }
            }
        });

        migrateBtn.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                if (deviceIdConn != null) {
                    continueAbilityReversibly(deviceIdConn);
                }
            }
        });

        migrateBackBtn.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                try {
                    reverseContinueAbility();
                } catch (Exception e) {

                }
            }
        });
    }

    private void showKeyBoard() {
        getUITaskDispatcher().delayDispatch(() -> textField.simulateClick(), SHOW_KEYBOARD_DELAY);
    }

    private void initConnManager(String deviceId) {
        connectManager = ConnectManagerIml.getInstance();
        connectManager.connectPa(this, deviceId);
    }


    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }


    @Override
    public boolean onStartContinuation() {
        return true;
    }

    @Override
    public boolean onSaveData(IntentParams intentParams) {
        intentParams.setParam("input", textField.getText());
        intentParams.setParam("plusA", plusA.getText());
        intentParams.setParam("plusB", plusB.getText());
        intentParams.setParam("plusResult",plusText.getText());
        return true;
    }

    @Override
    public boolean onRestoreData(IntentParams intentParams) {
        //这里内部嵌入了一个线程，要更新UI主线程中的组件上的值，要先拿到UI线程，然后投递任务Runnable过去
        //在任务Runnable中，向UI组件写值
        getUITaskDispatcher().asyncDispatch(new Runnable() {
            @Override
            public void run() {
                textField.setText(intentParams.getParam("input").toString());
                plusA.setText(intentParams.getParam("plusA").toString());
                plusB.setText(intentParams.getParam("plusB").toString());
                plusText.setText(intentParams.getParam("plusResult").toString());
            }
        });
        return true;
    }

    @Override
    public void onCompleteContinuation(int i) {
        new ToastDialog(RemoteControlAbilitySlice.this).setText("迁移成功").show();
    }
}
