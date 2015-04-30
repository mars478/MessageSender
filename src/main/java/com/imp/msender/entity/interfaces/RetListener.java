package com.imp.msender.entity.interfaces;

import java.io.Serializable;

public interface RetListener<RET, ARG> extends Serializable {

    public RET call(ARG arg);

}
