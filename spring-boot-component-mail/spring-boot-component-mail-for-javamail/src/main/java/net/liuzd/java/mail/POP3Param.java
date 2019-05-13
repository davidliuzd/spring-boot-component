package net.liuzd.java.mail;

import java.util.ArrayList;
import java.util.List;

import javax.mail.search.AndTerm;
import javax.mail.search.SearchTerm;

import lombok.Data;

@Data
public class POP3Param {

    /*
     * Folder.READ_ONLY：只读权限 Folder.READ_WRITE：可读可写（可以修改邮件的状态）
     */
    private int              readStats   = 1;

    /**
     * 大于等于消息号
     */
    private Integer          startMsgNum;

    /**
     * 是否过滤只读
     */
    private boolean          isFilterReadMail;

    private List<SearchTerm> searchTerms = new ArrayList<>();

    public void addSearchTerm(SearchTerm searchTerm) {
        searchTerms.add(searchTerm);
    }

    public AndTerm getAndTerm() {
        return new AndTerm(searchTerms.toArray(new SearchTerm[searchTerms.size()]));
    }

}
