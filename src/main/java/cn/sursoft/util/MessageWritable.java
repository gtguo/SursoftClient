package cn.sursoft.util;

import com.google.protobuf.GeneratedMessageV3;

interface MessageWritable {
    public void write(final GeneratedMessageV3 message);
}
