package io.turtle.example;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.Selector;
import java.nio.channels.WritableByteChannel;

/**
 * Created by gabriele on 18/03/15.
 */
public class PipeExample {

    public static void main(String[] args) throws IOException {
        Selector selector = Selector.open();
        Pipe pipe = Pipe.open();
        WritableByteChannel out = pipe.sink();
        ReadableByteChannel in = pipe.source();


        NumberProducer producer = new NumberProducer(out, 200);
        NumberConsumer consumer = new NumberConsumer(in);
        producer.start();
        consumer.start();
    }
}



class NumberConsumer extends Thread {

    private ReadableByteChannel in;

    public NumberConsumer(ReadableByteChannel in) {
        this.in = in;
    }

    public void run() {

        ByteBuffer sizeb = ByteBuffer.allocate(4);
        try {
            while (sizeb.hasRemaining())
                in.read(sizeb);
            sizeb.flip();
            int howMany = sizeb.getInt();
            sizeb.clear();

            for (int i = 0; i < howMany; i++) {
                while (sizeb.hasRemaining())
                    in.read(sizeb);
                sizeb.flip();
                int length = sizeb.getInt();
                sizeb.clear();

                ByteBuffer data = ByteBuffer.allocate(length);
                while (data.hasRemaining())
                    in.read(data);

                BigInteger result = new BigInteger(data.array());
                System.out.println(result);
            }
        } catch (IOException ex) {
            System.err.println(ex);
        } finally {
            try {
                in.close();
            } catch (Exception ex) {
                // We tried
            }
        }
    }
}

class NumberProducer extends Thread {

    private WritableByteChannel out;

    private int howMany;

    public NumberProducer(WritableByteChannel out, int howMany) {
        this.out = out;
        this.howMany = howMany;
    }

    public void run() {
        try {
            ByteBuffer buffer = ByteBuffer.allocate(4);
            buffer.putInt(this.howMany);
            buffer.flip();
            while (buffer.hasRemaining())
                out.write(buffer);

            for (int i = 0; i < howMany; i++) {
                byte[] data = new BigInteger(Integer.toString(i)).toByteArray();
                buffer = ByteBuffer.allocate(4 + data.length);

                buffer.putInt(data.length);
                buffer.put(data);
                buffer.flip();

                while (buffer.hasRemaining())
                    out.write(buffer);
            }
            out.close();
            System.err.println("Closed");
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }
}
