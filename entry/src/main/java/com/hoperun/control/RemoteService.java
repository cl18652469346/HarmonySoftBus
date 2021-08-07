package com.hoperun.control;

import com.hoperun.control.constants.EventConstants;
import com.hoperun.control.proxy.ConnectManagerIml;
import com.hoperun.control.utils.LogUtils;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;
import ohos.event.commonevent.CommonEventData;
import ohos.event.commonevent.CommonEventManager;
import ohos.rpc.*;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;

import static com.hoperun.control.proxy.ConnectManagerIml.*;

public class RemoteService extends Ability {
    public static final int ERR_OK = 0;
    private static final String TAG = RemoteService.class.getSimpleName();
    private MyRemote remote = new MyRemote();

    @Override
    public void onStart(Intent intent) {
        LogUtils.info(TAG, "RemoteService::onStart");
        super.onStart(intent);
    }

    @Override
    public void onBackground() {
        super.onBackground();
        LogUtils.info(TAG, "RemoteService::onBackground");
    }

    @Override
    public void onStop() {
        super.onStop();
        LogUtils.info(TAG, "RemoteService::onStop");
    }

    @Override
    public void onCommand(Intent intent, boolean isRestart, int startId) {
    }

    @Override
    protected IRemoteObject onConnect(Intent intent) {
        super.onConnect(intent);
        return remote.asObject();
    }

    @Override
    public void onDisconnect(Intent intent) {
        LogUtils.info(TAG, "RemoteService::onDisconnect");
    }

    /**
     * 远端请求处理
     *
     * @since 2021-02-25
     */
    public class MyRemote extends RemoteObject implements IRemoteBroker {
        private MyRemote() {
            super("===MyService_Remote");
        }

        @Override
        public IRemoteObject asObject() {
            return this;
        }

        @Override
        public boolean onRemoteRequest(int code, MessageParcel data, MessageParcel reply, MessageOption option) {
            LogUtils.info(TAG, "===onRemoteRequest......");
            int requestType = data.readInt();
            String inputString = "";
            if (code == REQUEST_SEND_DATA) {
                inputString = data.readString();
                publishInput(requestType, inputString);
            } else if (code == REQUEST_PLUS) {
                int a = data.readInt();
                int b = data.readInt();
                reply.writeInt(ERR_OK);
                reply.writeInt(a + b);
                publishPlusResult(requestType, String.valueOf(a + b));
            } else {

            }
            return true;
        }
    }

    private void publishInput(int requestType, String string) {
        LogUtils.info(TAG, "publishInput......");
        try {
            Intent intent = new Intent();
            Operation operation = new Intent.OperationBuilder()
                    .withAction(EventConstants.SCREEN_REMOTE_CONTROLL_EVENT)
                    .build();
            intent.setOperation(operation);
            intent.setParam("inputString", string);
            intent.setParam("requestType", requestType);
            CommonEventData eventData = new CommonEventData(intent);
            CommonEventManager.publishCommonEvent(eventData);
        } catch (RemoteException e) {
            LogUtils.error(TAG, "publishInput occur exception.");
        }
    }

    private void publishPlusResult(int requestType, String result) {
        LogUtils.info(TAG, "publishPlusResult......");
        try {
            Intent intent = new Intent();
            Operation operation = new Intent.OperationBuilder()
                    .withAction(EventConstants.SCREEN_REMOTE_CONTROLL_EVENT)
                    .build();
            intent.setOperation(operation);
            intent.setParam("plusResult", result);
            intent.setParam("requestType", requestType);
            CommonEventData eventData = new CommonEventData(intent);
            CommonEventManager.publishCommonEvent(eventData);
        } catch (RemoteException e) {
            LogUtils.error(TAG, "publishPlusResult occur exception.");
        }
    }
}