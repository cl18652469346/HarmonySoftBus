/*
 * Copyright (c) 2021 Huawei Device Co., Ltd.
 * Licensed under the Apache License,Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hoperun.control.proxy;

import com.hoperun.control.utils.LogUtils;
import ohos.rpc.*;

import java.util.Map;

/**
 * 远程连接代理类
 */
public class MyRemoteProxy implements IRemoteBroker {
    /**
     * 远端响应成功的标识
     */
    public static final int ERR_OK = 0;
    private static final String TAG = MyRemoteProxy.class.getSimpleName();
    private final IRemoteObject remote;
    public MyRemoteProxy(IRemoteObject remote) {
        this.remote = remote;
    }

    @Override
    public IRemoteObject asObject() {
        return remote;
    }

    public int senDataToRemote(int requestType, Map paramMap) {
        MessageParcel data = MessageParcel.obtain();
        MessageParcel reply = MessageParcel.obtain();
        MessageOption option = new MessageOption(MessageOption.TF_SYNC);
        int ec = 1;
        int result = -1;
        try {
            if (paramMap.get("inputString") instanceof String) {
                String inputString = (String) paramMap.get("inputString");
                data.writeInt(requestType);
                data.writeString(inputString);
                remote.sendRequest(requestType, data, reply, option);
            }

            if (requestType == ConnectManagerIml.REQUEST_PLUS) {
                data.writeInt(requestType);
                data.writeInt(Integer.parseInt((String) paramMap.get("plusA")));
                data.writeInt(Integer.parseInt((String) paramMap.get("plusB")));
                remote.sendRequest(requestType, data, reply, option);
            } else if (requestType == ConnectManagerIml.REQUEST_START_PLAY ||
                    requestType == ConnectManagerIml.REQUEST_PAUSE_PLAY) {
                data.writeInt(requestType);
                remote.sendRequest(requestType, data, reply, option);
            } else {

            }

            ec = reply.readInt();
            if (ec != ERR_OK) {
                LogUtils.error(TAG, "RemoteException:");
            } else {
                if (requestType == ConnectManagerIml.REQUEST_PLUS) {
                    result = reply.readInt();
                }
            }
        } catch (RemoteException e) {
            LogUtils.error(TAG, "RemoteException:");
        } finally {
            ec = ERR_OK;
            if (result != -1) {
                ec = result;
            }
            data.reclaim();
            reply.reclaim();
        }
        return ec;
    }
}
