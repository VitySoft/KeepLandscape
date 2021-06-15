package com.vitysoft.android.keeplandscape

import android.os.Build

val Any.TAG: String
    get() {
        var name: String
        if (javaClass.isAnonymousClass) {
            name = javaClass.name
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N
                && name.length > 23
            ) {
                // 匿名类取后23个字符
                name = name.substring(name.length - 23, name.length)
            }
        } else {
            name = javaClass.simpleName
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N
                && name.length > 23
            ) {
                // 正常类，取前23个字符
                name = name.substring(0, 23)
            }
        }
        return name
    }
