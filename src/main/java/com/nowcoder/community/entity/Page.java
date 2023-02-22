package com.nowcoder.community.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Page {
    private int current = 1;
    private int limit = 10;
    private int rows;
    private String path;

    public void setCurrent(int current) {
        if(current >= 1){
            this.current = current;
        }
    }

    public void setLimit(int limit) {
        if(limit >= 1 && limit <= 100){
            this.limit = limit;
        }
    }

    public void setRows(int rows) {
        if(rows >= 0){
            this.rows = rows;
        }
    }

    public int getOffset(){
        return (current - 1) * limit;
    }

    public int getTotal(){
        if(rows % limit == 0){
            return rows / limit;
        }else {
            return rows / limit + 1;
        }
    }

    public int getFrom(){
        int from = current - 2;
        return Math.max(1, from);
    }

    public int getTo(){
        int to = current + 2;
        int total = getTotal();
        return Math.min(to, total);
    }
}
