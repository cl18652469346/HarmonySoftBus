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

package com.hoperun.control.utils;

import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;

/**
 * 日志工具类
 */
public class LogUtils {
    private static final String TAG_LOG = "LogUtil";

    private static final HiLogLabel LABEL_LOG = new HiLogLabel(0, 0, LogUtils.TAG_LOG);

    private static final String LOG_FORMAT = "%{public}s: %{public}s";

    private LogUtils() {
    }

    /**
     * 打印调试日志
     *
     * @param tag 日志标签
     * @param msg 日志信息
     */
    public static void debug(String tag, String msg) {
        HiLog.debug(LABEL_LOG, LOG_FORMAT, tag, msg);
    }

    /**
     * 打印信息日志
     *
     * @param tag 日志标签
     * @param msg 日志信息
     */
    public static void info(String tag, String msg) {
        HiLog.info(LABEL_LOG, LOG_FORMAT, tag, msg);
    }

    /**
     * 打印警告日志
     *
     * @param tag 日志标签
     * @param msg 日志信息
     */
    public static void warn(String tag, String msg) {
        HiLog.warn(LABEL_LOG, LOG_FORMAT, tag, msg);
    }

    /**
     * 打印错误日志
     *
     * @param tag 日志标签
     * @param msg 日志信息
     */
    public static void error(String tag, String msg) {
        HiLog.error(LABEL_LOG, LOG_FORMAT, tag, msg);
    }
}
