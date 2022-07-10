package com.ubi.android.models;

import java.io.Serializable;

public class ChatRequestModel implements Serializable {
    public String deviceId, mediaType, message, receiverId, receiverName, senderId, senderName,
            timeStamp;

}
