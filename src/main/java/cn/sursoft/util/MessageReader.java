package cn.sursoft.util;

import java.io.IOException;
import java.io.InputStream;
import cn.sursoft.Wire;
class MessageReader {
    private InputStream in;

    public MessageReader(InputStream in) {
        this.in = in;
    }

    public Wire.Envelope read() throws IOException {
        return Wire.Envelope.parseDelimitedFrom(in);
    }
}
