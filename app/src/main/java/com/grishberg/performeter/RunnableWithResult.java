package com.grishberg.performeter;

/**
 * Runnable with some result getter.
 */
public interface RunnableWithResult extends Runnable {
    Object getResult();
}
