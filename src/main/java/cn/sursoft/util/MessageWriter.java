package cn.sursoft.util;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.sursoft.Wire;

class MessageWriter{
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private OutputStream out;

    public MessageWriter(OutputStream out) {
        this.out = out;
    }

    public void write(Wire.Envelope envelope) {
        executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    envelope.writeDelimitedTo(out);
                }
                catch (IOException e) {
                    // The socket went away
                    executor.shutdownNow();
                }
            }
        });
    }
}
