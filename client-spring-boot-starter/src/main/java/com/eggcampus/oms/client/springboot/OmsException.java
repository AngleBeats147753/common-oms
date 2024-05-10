package com.eggcampus.oms.client.springboot;

import com.eggcampus.util.exception.EggCampusException;

/**
 * @author 黄磊
 */
public class OmsException extends EggCampusException {
    public OmsException() {
        super();
    }

    public OmsException(String message) {
        super(message);
    }

    public OmsException(String message, Throwable cause) {
        super(message, cause);
    }

    public OmsException(Throwable cause) {
        super(cause);
    }

    protected OmsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
