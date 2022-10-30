package com.bluejungle.destiny.mgmtconsole.status;

import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class ThreadDumpManager implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Map<Thread, StackTraceElement[]> stackTraces;

    public ThreadDumpManager() {
        stackTraces = new TreeMap<Thread, StackTraceElement[]>(THREAD_COMPARATOR);
        stackTraces.putAll(Thread.getAllStackTraces());
    }

    public Collection<Thread> getThreads() {
        return stackTraces.keySet();
    }

    public Map<Thread, StackTraceElement[]> getStackTraces() {
        return stackTraces;
    }

    private static final Comparator<Thread> THREAD_COMPARATOR = 
        new Comparator<Thread>() {
            public int compare(Thread t1, Thread t2) {
                int result = t1.getName().compareTo(t2.getName());
                if (result == 0) {
                    Long id1 = t1.getId();
                    Long id2 = t2.getId();
                    return id1.compareTo(id2);
                }
                return result;
            }
        };
}
