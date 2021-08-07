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

package com.hoperun.control.adapter;

import com.hoperun.control.ResourceTable;
import ohos.agp.components.*;
import ohos.app.Context;
import ohos.distributedschedule.interwork.DeviceInfo;

import java.util.List;
import java.util.Optional;

/**
 * 设备列表适配器
 */
public class DevicesListAdapter extends BaseItemProvider {
    private static final int LENGTH = 4;
    private List<DeviceInfo> deviceInfoList;
    private Context context;

    /**
     * 设备列表适配器构造方法
     *
     * @param listBasicInfo 基础列表信息
     * @param context 上下文
     * @since 2021-2-25
     */
    public DevicesListAdapter(List<DeviceInfo> listBasicInfo, Context context) {
        this.deviceInfoList = listBasicInfo;
        this.context = context;
    }

    @Override
    public int getCount() {
        return deviceInfoList == null ? 0 : deviceInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return Optional.of(deviceInfoList.get(position));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Component getComponent(int position, Component componentP, ComponentContainer componentContainer) {
        Component component = componentP;
        if (component == null) {
            component = LayoutScatter.getInstance(context).parse(ResourceTable.Layout_item_device_list, null, false);
            ViewHolder viewHolder = new ViewHolder();
            if (component.findComponentById(ResourceTable.Id_device_name) instanceof Text) {
                viewHolder.devicesName = (Text) component.findComponentById(ResourceTable.Id_device_name);
            }
            if (component.findComponentById(ResourceTable.Id_device_id) instanceof Text) {
                viewHolder.devicesId = (Text) component.findComponentById(ResourceTable.Id_device_id);
            }
            viewHolder.devicesName.setText(deviceInfoList.get(position).getDeviceName());
            String deviceId = deviceInfoList.get(position).getDeviceId();
            String stringReplace = "****** ";
            viewHolder.devicesId.setText(deviceId.substring(0, LENGTH)
                    + stringReplace.substring(0, stringReplace.length() - 1)
                    + deviceId.substring(deviceId.length() - LENGTH));
            component.setTag(viewHolder);
        }
        return component;
    }

    /**
     * 数据封装类
     *
     * @since 2021-03-12
     */
    private static class ViewHolder {
        private Text devicesName;
        private Text devicesId;
    }
}
