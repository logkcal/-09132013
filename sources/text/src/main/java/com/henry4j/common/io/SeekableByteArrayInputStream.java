package com.henry4j.common.io;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;

import org.apache.hadoop.fs.PositionedReadable;
import org.apache.hadoop.fs.Seekable;

public class SeekableByteArrayInputStream extends ByteArrayInputStream
        implements PositionedReadable, Seekable {
    public SeekableByteArrayInputStream(byte[] buf) {
        super(buf);
    }

    public SeekableByteArrayInputStream(byte[] buf, int offset, int length) {
        super(buf, offset, length);
    }

    @Override
    public synchronized int read(long position, byte[] buffer, int offset, int length) throws IOException {
        long oldPos = getPos();
        try {
            seek(position);
            return read(buffer, offset, length);
        } finally {
            seek(oldPos);
        }
    }

    @Override
    public synchronized void readFully(long position, byte[] buffer, int offset, int length) throws IOException {
        long oldPos = getPos();
        try {
            for (int read = 0; length > 0; length -= read) {
                seek(position + read);
                if ((read = read(buffer, offset + read, length)) < 0) {
                    throw new EOFException("EOF reached before reading fully.");
                }
            }
        } finally {
            seek(oldPos);
        }
    }

    @Override
    public void readFully(long position, byte[] buffer) throws IOException {
        readFully(position, buffer, 0, buffer.length);
    }

    @Override
    public void seek(long position)
            throws IOException {
        if (position < 0) {
            throw new IllegalArgumentException("position must be positive (0 or greater).");
        }
        if (position > count) {
            throw new IllegalArgumentException("position cannot be greater than 'count'.");
        }
        pos = (int)position;
    }

    @Override
    public long getPos() throws IOException {
        return pos;
    }

    @Override
    public boolean seekToNewSource(long targetPos)
            throws IOException {
        return false;
    }
}
