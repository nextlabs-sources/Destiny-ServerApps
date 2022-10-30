/**
 * 
 */
package com.nextlabs.destiny.inquirycenter.migration;

import com.nextlabs.destiny.container.dac.datasync.IDataSyncTaskUpdate;

/**
 * @author nnallagatla
 *
 */
public class MigrationTaskUpdate implements IDataSyncTaskUpdate {

    protected int goodCount = 0;
    protected int badCount = 0;
    protected int total = 0;
    protected String prefix = "";
    
    public long getUpdateInterval() {
        return 10000000;
    }

    private void precheck() throws IllegalStateException {
        if (!alive()) {
            throw new IllegalStateException();
        }
    }

    private void postUpdate() throws IllegalStateException {
    	//do nothing
    }
    
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
    
    public void addFail(int size) throws IllegalStateException {
        precheck();
        badCount += size;
        postUpdate();
    }

    public void addSuccess(int size) throws IllegalStateException {
        precheck();
        goodCount += size;
        /*if (getLog().isInfoEnabled()) {
            StringBuffer buffer = new StringBuffer();
            buffer.append("Progress: ").append(goodCount).append('/').append(total);
            getLog().info(buffer.toString());
        }*/
        postUpdate();
    }

    public void setTotalSize(int size) throws IllegalStateException {
        precheck();
        this.total = size;
        postUpdate();
    }
    
    public void reset(){
        goodCount = 0;
        badCount = 0;
        total = 0;
        prefix = "";
    }

    public boolean alive() {
    	return true;
    }

    private static final String FORMAT = "%d/%d/%d";

    @Override
    public String toString() {
        return prefix + " " + String.format(FORMAT, total, goodCount, badCount);
    }


}
