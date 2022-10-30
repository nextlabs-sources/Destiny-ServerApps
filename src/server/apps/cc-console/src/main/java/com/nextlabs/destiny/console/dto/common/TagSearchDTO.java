/**
 * 
 */
package com.nextlabs.destiny.console.dto.common;

/**
 * @author kyu
 *
 */
public class TagSearchDTO {
    private TagDTO tag;
    private boolean showHidden;
    private int pageNo;
    private int pageSize = 10;

    public TagDTO getTag() {
        return tag;
    }

    public void setTag(TagDTO tag) {
        this.tag = tag;
    }

    public boolean isShowHidden() {
        return showHidden;
    }

    public void setShowHidden(boolean showHidden) {
        this.showHidden = showHidden;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

}
