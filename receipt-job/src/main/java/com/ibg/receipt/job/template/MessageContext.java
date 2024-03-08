package com.ibg.receipt.job.template;

import java.util.HashMap;
import java.util.Map;

public class MessageContext extends HashMap<String, Object> {

    private static final long serialVersionUID = 9208177481889673898L;

    public static MessageContext newInstance() {
        return new MessageContext();
    }

    public static MessageContext newInstance(final Map<String, Object> map) {
        MessageContext context = new MessageContext();
        context.putAll(map);
        return context;
    }
    
    private MessageContext() {}
}