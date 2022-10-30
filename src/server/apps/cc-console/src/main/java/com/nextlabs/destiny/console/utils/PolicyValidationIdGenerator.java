/**
 * 
 */
package com.nextlabs.destiny.console.utils;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author kyu
 * @since 8.0.8
 *
 */
public final class PolicyValidationIdGenerator {
    
    private static final AtomicLong id = new AtomicLong(System.nanoTime());
    
    public static Long generateId() {
        return id.getAndIncrement();
    }
}
