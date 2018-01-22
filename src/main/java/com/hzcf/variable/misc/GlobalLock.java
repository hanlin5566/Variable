package com.hzcf.variable.misc;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class GlobalLock {
    public static ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock(true);

    public static ReentrantReadWriteLock.ReadLock readLock = reentrantReadWriteLock.readLock();

    public static ReentrantReadWriteLock.WriteLock writeLock = reentrantReadWriteLock.writeLock();
}
