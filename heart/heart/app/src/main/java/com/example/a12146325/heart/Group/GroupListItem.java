package com.example.a12146325.heart.Group;

/**
 * Created by 김준성 on 2015-07-17.
 */
public class GroupListItem {
    private String name;

    /**
     * 그룹리스트아이템 생성자
     * @param name 그룹명

     */
    public GroupListItem(String name) {
        this.name = name;

    }

    /**
     * 그룹명을 가져오는 메소드
     * @return 그룹명
     */
    public String getName() {
        return this.name;
    }


}
