package com.ubi.android.models;

import java.io.Serializable;

public class BaseResponseExtended implements Serializable {
    public String message, type;
    public int code;
    public DevMessage dev_message;
}
