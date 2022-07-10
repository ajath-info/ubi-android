package com.ubi.android.models;

import java.io.Serializable;

public class BaseResponse implements Serializable {
    public String message, type, dev_message;
    public int code;
}
